mod occlusion;

use jni::objects::{JByteArray, JClass};
use jni::sys::{jint, jintArray};
use jni::JNIEnv;

/// Occupancy layout is x + width * (z + depth * y).
fn is_solid(voxels: &[u8], dims: [usize; 3], p: [isize; 3]) -> bool {
    if p[0] < 0 || p[1] < 0 || p[2] < 0
        || p[0] >= dims[0] as isize
        || p[1] >= dims[1] as isize
        || p[2] >= dims[2] as isize {
        return false;
    }
    let (x, y, z) = (p[0] as usize, p[1] as usize, p[2] as usize);
    voxels[x + dims[0] * (z + dims[2] * y)] != 0
}

/// Greedy opaque-cube mesher. Each output record is:
/// [block_x, block_y, block_z, face_id, width, height].
/// Face IDs: 0=-X, 1=+X, 2=-Y, 3=+Y, 4=-Z, 5=+Z.
/// Width/height span the two axes perpendicular to the face.
fn build_greedy_faces(voxels: &[u8], width: usize, height: usize, depth: usize) -> Vec<i32> {
    let dims = [width, height, depth];
    let mut output = Vec::new();

    for face in 0..6 {
        let (axis, positive, u_axis, v_axis) = match face {
            0 => (0, false, 2, 1),
            1 => (0, true, 2, 1),
            2 => (1, false, 0, 2),
            3 => (1, true, 0, 2),
            4 => (2, false, 0, 1),
            _ => (2, true, 0, 1),
        };
        let u_size = dims[u_axis];
        let v_size = dims[v_axis];

        for slice in 0..dims[axis] {
            let mut mask = vec![false; u_size * v_size];
            for v in 0..v_size {
                for u in 0..u_size {
                    let mut p = [0isize; 3];
                    p[axis] = slice as isize;
                    p[u_axis] = u as isize;
                    p[v_axis] = v as isize;
                    if !is_solid(voxels, dims, p) { continue; }
                    let mut neighbor = p;
                    neighbor[axis] += if positive { 1 } else { -1 };
                    mask[u + u_size * v] = !is_solid(voxels, dims, neighbor);
                }
            }

            for v in 0..v_size {
                let mut u = 0;
                while u < u_size {
                    let idx = u + u_size * v;
                    if !mask[idx] {
                        u += 1;
                        continue;
                    }

                    let mut rect_width = 1;
                    while u + rect_width < u_size && mask[u + rect_width + u_size * v] {
                        rect_width += 1;
                    }

                    let mut rect_height = 1;
                    'grow: while v + rect_height < v_size {
                        for du in 0..rect_width {
                            if !mask[u + du + u_size * (v + rect_height)] {
                                break 'grow;
                            }
                        }
                        rect_height += 1;
                    }

                    for dv in 0..rect_height {
                        for du in 0..rect_width {
                            mask[u + du + u_size * (v + dv)] = false;
                        }
                    }

                    let mut origin = [0usize; 3];
                    origin[axis] = slice;
                    origin[u_axis] = u;
                    origin[v_axis] = v;
                    output.extend_from_slice(&[
                        origin[0] as i32, origin[1] as i32, origin[2] as i32,
                        face as i32, rect_width as i32, rect_height as i32,
                    ]);
                    u += rect_width;
                }
            }
        }
    }
    output
}

fn java_int_array(mut env: JNIEnv, values: &[i32]) -> jintArray {
    if values.len() > i32::MAX as usize { return std::ptr::null_mut(); }
    let array = match env.new_int_array(values.len() as i32) {
        Ok(array) => array,
        Err(_) => return std::ptr::null_mut(),
    };
    if env.set_int_array_region(&array, 0, values).is_err() {
        return std::ptr::null_mut();
    }
    array.into_raw()
}

/// JNI entrypoint for dev.flux.nativebridge.NativeBridge.findFullyOccludedBlocksNative.
#[no_mangle]
pub extern "system" fn Java_dev_flux_nativebridge_NativeBridge_findFullyOccludedBlocksNative(
    mut env: JNIEnv,
    _class: JClass,
    voxels: JByteArray,
    width: jint,
    height: jint,
    depth: jint,
) -> jni::sys::jbyteArray {
    if width <= 0 || height <= 0 || depth <= 0 { return std::ptr::null_mut(); }
    let (w, h, d) = (width as usize, height as usize, depth as usize);
    let expected = match w.checked_mul(h).and_then(|v| v.checked_mul(d)) {
        Some(value) if value <= i32::MAX as usize => value,
        _ => return std::ptr::null_mut(),
    };
    let data = match env.convert_byte_array(&voxels) {
        Ok(data) if data.len() == expected => data,
        _ => return std::ptr::null_mut(),
    };
    let mask = occlusion::fully_occluded_mask(&data, w, h, d);
    let output = match env.new_byte_array(mask.len() as i32) {
        Ok(array) => array,
        Err(_) => return std::ptr::null_mut(),
    };
    if env.set_byte_array_region(&output, 0, &mask).is_err() {
        return std::ptr::null_mut();
    }
    output.into_raw()
}

/// JNI entrypoint for dev.flux.nativebridge.NativeBridge.buildGreedyOpaqueMeshNative.
#[no_mangle]
pub extern "system" fn Java_dev_flux_nativebridge_NativeBridge_buildGreedyOpaqueMeshNative(
    mut env: JNIEnv,
    _class: JClass,
    voxels: JByteArray,
    width: jint,
    height: jint,
    depth: jint,
) -> jintArray {
    if width <= 0 || height <= 0 || depth <= 0 { return std::ptr::null_mut(); }
    let (w, h, d) = (width as usize, height as usize, depth as usize);
    let expected = match w.checked_mul(h).and_then(|v| v.checked_mul(d)) {
        Some(value) if value <= i32::MAX as usize => value,
        _ => return std::ptr::null_mut(),
    };
    let data = match env.convert_byte_array(&voxels) {
        Ok(data) if data.len() == expected => data,
        _ => return std::ptr::null_mut(),
    };
    let faces = build_greedy_faces(&data, w, h, d);
    java_int_array(env, &faces)
}

#[cfg(test)]
mod tests {
    use super::build_greedy_faces;

    #[test]
    fn one_solid_voxel_has_six_quads() {
        assert_eq!(build_greedy_faces(&[1], 1, 1, 1).len(), 36);
    }

    #[test]
    fn adjacent_voxels_hide_shared_face_and_merge_coplanar_faces() {
        let mesh = build_greedy_faces(&[1, 1], 2, 1, 1);
        assert_eq!(mesh.len(), 36); // Six merged quads, not ten individual faces.
    }

    #[test]
    fn solid_cube_collapses_to_six_quads() {
        let mesh = build_greedy_faces(&[1; 8], 2, 2, 2);
        assert_eq!(mesh.len(), 36);
    }

    #[test]
    fn empty_voxels_have_no_quads() {
        assert!(build_greedy_faces(&[0; 8], 2, 2, 2).is_empty());
    }
}
