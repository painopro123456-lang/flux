use jni::objects::{JByteArray, JClass};
use jni::sys::{jint, jintArray};
use jni::JNIEnv;

/// Occupancy layout is x + width * (z + depth * y).
/// Each output face is four integers: block_x, block_y, block_z, face_id.
/// Face IDs: 0=-X, 1=+X, 2=-Y, 3=+Y, 4=-Z, 5=+Z.
fn build_surface_faces(voxels: &[u8], width: usize, height: usize, depth: usize) -> Vec<i32> {
    let index = |x: usize, y: usize, z: usize| x + width * (z + depth * y);
    let solid = |x: isize, y: isize, z: isize| -> bool {
        if x < 0 || y < 0 || z < 0
            || x >= width as isize || y >= height as isize || z >= depth as isize {
            return false;
        }
        voxels[index(x as usize, y as usize, z as usize)] != 0
    };

    let mut faces = Vec::new();
    for y in 0..height {
        for z in 0..depth {
            for x in 0..width {
                if !solid(x as isize, y as isize, z as isize) { continue; }
                let neighbors = [
                    (x as isize - 1, y as isize, z as isize),
                    (x as isize + 1, y as isize, z as isize),
                    (x as isize, y as isize - 1, z as isize),
                    (x as isize, y as isize + 1, z as isize),
                    (x as isize, y as isize, z as isize - 1),
                    (x as isize, y as isize, z as isize + 1),
                ];
                for (face, (nx, ny, nz)) in neighbors.into_iter().enumerate() {
                    if !solid(nx, ny, nz) {
                        faces.extend_from_slice(&[x as i32, y as i32, z as i32, face as i32]);
                    }
                }
            }
        }
    }
    faces
}

/// JNI entrypoint for dev.flux.nativebridge.NativeBridge.buildOpaqueMeshNative.
#[no_mangle]
pub extern "system" fn Java_dev_flux_nativebridge_NativeBridge_buildOpaqueMeshNative(
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
    let faces = build_surface_faces(&data, w, h, d);
    if faces.len() > i32::MAX as usize { return std::ptr::null_mut(); }
    let output = match env.new_int_array(faces.len() as i32) {
        Ok(array) => array,
        Err(_) => return std::ptr::null_mut(),
    };
    if env.set_int_array_region(&output, 0, &faces).is_err() { return std::ptr::null_mut(); }
    output.into_raw()
}

#[cfg(test)]
mod tests {
    use super::build_surface_faces;

    #[test]
    fn one_solid_voxel_has_six_faces() {
        assert_eq!(build_surface_faces(&[1], 1, 1, 1).len(), 24);
    }

    #[test]
    fn adjacent_voxels_hide_their_shared_face() {
        assert_eq!(build_surface_faces(&[1, 1], 2, 1, 1).len(), 40);
    }

    #[test]
    fn empty_voxels_have_no_faces() {
        assert!(build_surface_faces(&[0; 8], 2, 2, 2).is_empty());
    }
}
