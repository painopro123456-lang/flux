/// Returns one byte per voxel. A value of 1 marks a solid voxel whose six
/// face-neighbours are also solid. Section edges are treated as exposed.
pub fn fully_occluded_mask(voxels: &[u8], width: usize, height: usize, depth: usize) -> Vec<i8> {
    let index = |x: usize, y: usize, z: usize| x + width * (z + depth * y);
    let solid = |x: isize, y: isize, z: isize| -> bool {
        if x < 0 || y < 0 || z < 0
            || x >= width as isize || y >= height as isize || z >= depth as isize {
            return false;
        }
        voxels[index(x as usize, y as usize, z as usize)] != 0
    };

    let mut mask = vec![0i8; voxels.len()];
    for y in 0..height {
        for z in 0..depth {
            for x in 0..width {
                if !solid(x as isize, y as isize, z as isize) { continue; }
                let p = (x as isize, y as isize, z as isize);
                let neighbours = [
                    (p.0 - 1, p.1, p.2), (p.0 + 1, p.1, p.2),
                    (p.0, p.1 - 1, p.2), (p.0, p.1 + 1, p.2),
                    (p.0, p.1, p.2 - 1), (p.0, p.1, p.2 + 1),
                ];
                if neighbours.into_iter().all(|n| solid(n.0, n.1, n.2)) {
                    mask[index(x, y, z)] = 1;
                }
            }
        }
    }
    mask
}

#[cfg(test)]
mod tests {
    use super::fully_occluded_mask;

    #[test]
    fn single_voxel_is_not_occluded() {
        assert_eq!(fully_occluded_mask(&[1], 1, 1, 1), vec![0]);
    }

    #[test]
    fn center_of_solid_three_cube_is_occluded() {
        let mask = fully_occluded_mask(&[1; 27], 3, 3, 3);
        assert_eq!(mask[1 + 3 * (1 + 3)], 1);
        assert_eq!(mask.iter().filter(|&&value| value == 1).count(), 1);
    }

    #[test]
    fn empty_voxel_is_never_occluded() {
        let mut voxels = [1; 27];
        voxels[13] = 0;
        let mask = fully_occluded_mask(&voxels, 3, 3, 3);
        assert_eq!(mask[13], 0);
    }
}
