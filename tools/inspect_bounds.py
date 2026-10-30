"""Print the world-space bounding box of every part so proportions can be judged numerically."""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'tools'))

import preview_models as pm  # noqa: E402


def main() -> None:
    for name in ('SpiritFoxModel', 'WindWolfModel', 'StonehornGoatModel',
                 'CrystalTurtleModel', 'EmberCraneModel'):
        model = pm.parse_model(pm.MODELS / f'{name}.java')
        cubes = []
        pm.collect(model, [[1, 0, 0], [0, 1, 0], [0, 0, 1]], [0.0, 0.0, 0.0], cubes)

        print('=====', name)
        boxes = {}
        for box, m, offset, part in cubes:
            lo = [1e9] * 3
            hi = [-1e9] * 3
            for face, corners in pm.face_quads(box).items():
                for corner in corners:
                    local = pm.apply(m, list(corner))
                    world = [local[i] + offset[i] for i in range(3)]
                    for i in range(3):
                        lo[i] = min(lo[i], world[i])
                        hi[i] = max(hi[i], world[i])
            key = part
            if key not in boxes:
                boxes[key] = [lo, hi]
            else:
                for i in range(3):
                    boxes[key][0][i] = min(boxes[key][0][i], lo[i])
                    boxes[key][1][i] = max(boxes[key][1][i], hi[i])

        for part in sorted(boxes):
            lo, hi = boxes[part]
            size = [hi[i] - lo[i] for i in range(3)]
            print(f'  {part:<16} x[{lo[0]:6.1f},{hi[0]:6.1f}] '
                  f'y[{lo[1]:6.1f},{hi[1]:6.1f}] z[{lo[2]:6.1f},{hi[2]:6.1f}] '
                  f'size({size[0]:.1f},{size[1]:.1f},{size[2]:.1f})')

        all_lo = [min(b[0][i] for b in boxes.values()) for i in range(3)]
        all_hi = [max(b[1][i] for b in boxes.values()) for i in range(3)]
        print('  TOTAL size', [round(all_hi[i] - all_lo[i], 1) for i in range(3)],
              'y range', round(all_lo[1], 1), '->', round(all_hi[1], 1))


if __name__ == '__main__':
    main()
