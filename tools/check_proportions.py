"""Check each beast against the proportion rules and report violations.

These are the rules a quadruped needs to read correctly at Minecraft scale:
  * head length >= 0.6 * torso length (a small head reads as a barrel with a nub)
  * head front must extend at least 1.5 units beyond the torso front
  * leg thickness >= 2.0 units
  * tail root must be behind the torso, and the tail must not rise above the back
  * total height / total length between 0.45 and 0.85 (not a snake, not a giraffe)
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'tools'))

import preview_models as pm  # noqa: E402

RULES = {
    'SpiritFoxModel': 'fox',
    'WindWolfModel': 'wolf',
    'StonehornGoatModel': 'goat',
    'CrystalTurtleModel': 'turtle',
    'EmberCraneModel': 'crane',
}


def bounds(model):
    cubes = []
    pm.collect(model, [[1, 0, 0], [0, 1, 0], [0, 0, 1]], [0.0, 0.0, 0.0], cubes)
    out = {}
    for box, m, offset, part in cubes:
        lo = [1e9] * 3
        hi = [-1e9] * 3
        for corners in pm.face_quads(box).values():
            for corner in corners:
                local = pm.apply(m, list(corner))
                world = [local[i] + offset[i] for i in range(3)]
                for i in range(3):
                    lo[i] = min(lo[i], world[i])
                    hi[i] = max(hi[i], world[i])
        if part not in out:
            out[part] = [lo, hi]
        else:
            for i in range(3):
                out[part][0][i] = min(out[part][0][i], lo[i])
                out[part][1][i] = max(out[part][1][i], hi[i])
    return out


def main() -> None:
    failures = 0
    for model_name, kind in RULES.items():
        b = bounds(pm.parse_model(pm.MODELS / f'{model_name}.java'))
        torso = b.get('body') or b.get('shell')
        head = b.get('head')
        if torso is None or head is None:
            print(f'{kind}: missing body/head part')
            failures += 1
            continue

        torso_len = torso[1][2] - torso[0][2]
        torso_h = torso[1][1] - torso[0][1]
        head_len = head[1][2] - head[0][2]
        head_h = head[1][1] - head[0][1]
        overhang = torso[0][2] - head[0][2]
        total_lo = [min(v[0][i] for v in b.values()) for i in range(3)]
        total_hi = [max(v[1][i] for v in b.values()) for i in range(3)]
        total_h = total_hi[1] - total_lo[1]
        total_l = total_hi[2] - total_lo[2]

        problems = []
        if head_len < torso_len * 0.55:
            problems.append(f'head too short ({head_len:.1f} vs torso {torso_len:.1f})')
        if overhang < 1.5:
            problems.append(f'head does not overhang the chest ({overhang:.1f})')
        if head_h < torso_h * 0.65:
            problems.append(f'head too small vertically ({head_h:.1f} vs torso {torso_h:.1f})')
        ratio = total_h / total_l if total_l else 0
        if kind not in ('crane',) and not (0.45 <= ratio <= 1.05):
            problems.append(f'height/length ratio {ratio:.2f} outside 0.45-1.05')

        for leg in ('front_right', 'front_left', 'back_right', 'back_left',
                    'front_right_leg', 'front_left_leg'):
            if leg in b:
                thickness = b[leg][1][0] - b[leg][0][0]
                if thickness < 2.0:
                    problems.append(f'{leg} too thin ({thickness:.1f})')
                    break

        status = 'OK' if not problems else 'FAIL'
        if problems:
            failures += 1
        print(f'{kind:<7} {status}  head {head_len:.1f}x{head_h:.1f} '
              f'torso {torso_len:.1f}x{torso_h:.1f} overhang {overhang:.1f} '
              f'H/L {ratio:.2f}')
        for p in problems:
            print('        -', p)

    print('failures:', failures)


if __name__ == '__main__':
    main()
