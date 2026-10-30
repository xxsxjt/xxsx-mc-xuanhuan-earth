"""Second proportion pass, driven by what the four-view sheets actually showed.

Issues observed in tmp/model-views:
* Fox and wolf heads sit too low and too far back, so the torso front face hides them.
* Wolf tail pivots too low, making the tail stand almost vertical in the top view.
* Turtle shell is taller than it is wide relative to the limbs; head barely clears the rim.
* Goat horns still read as a straight X from the front instead of curling back.
* Crane wings stick straight out like a coat hanger and the legs are too long.
"""

from __future__ import annotations

import io
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SPEC = ROOT / 'tools' / 'rebuild_beast_models.py'


def patch(text: str, pairs: list[tuple[str, str]]) -> str:
    for old, new in pairs:
        if old not in text:
            print('MISS', old.splitlines()[0][:70])
            continue
        text = text.replace(old, new, 1)
    return text


def main() -> None:
    s = io.open(SPEC, encoding='utf-8').read()

    # ---- Fox: raise and push the head forward so it clears the torso.
    s = patch(s, [
        ('head = PartSpec("head", (0.0, 10.4, -5.0), boxes=[\n'
         '        BoxSpec("skull", -3.0, -2.6, -3.2, 6.0, 5.2, 6.2, "fur"),',
         'head = PartSpec("head", (0.0, 9.6, -6.0), boxes=[\n'
         '        BoxSpec("skull", -3.0, -2.6, -3.4, 6.0, 5.2, 6.6, "fur"),'),
        # Neck bridge so the raised head is not floating.
        ('    parts = [body, head]\n'
         '    for name, x, z in (("front_right", -2.0, -2.8)',
         '    neck = PartSpec("neck", (0.0, 11.0, -4.4), (0.34, 0.0, 0.0), [\n'
         '        BoxSpec("bridge", -2.3, -2.2, -1.8, 4.6, 4.4, 4.0, "fur"),\n'
         '    ])\n'
         '    parts = [body, neck, head]\n'
         '    for name, x, z in (("front_right", -2.0, -2.8)'),
    ])

    # ---- Wolf: same head fix, plus lift the tail root.
    s = patch(s, [
        ('head = PartSpec("head", (0.0, 9.8, -6.2), boxes=[\n'
         '        BoxSpec("skull", -3.2, -3.0, -3.4, 6.4, 6.0, 6.6, "fur"),',
         'head = PartSpec("head", (0.0, 9.0, -7.2), boxes=[\n'
         '        BoxSpec("skull", -3.2, -3.0, -3.6, 6.4, 6.0, 7.0, "fur"),'),
        ('    parts = [body, mane, head, collar]',
         '    neck = PartSpec("neck", (0.0, 10.8, -5.0), (0.36, 0.0, 0.0), [\n'
         '        BoxSpec("bridge", -2.8, -2.6, -2.0, 5.6, 5.2, 4.6, "fur"),\n'
         '    ])\n'
         '    parts = [body, neck, mane, head, collar]'),
        ('tail = PartSpec("tail", (0.0, 12.0, 5.0), (0.52, 0.0, 0.0), [',
         'tail = PartSpec("tail", (0.0, 10.4, 5.0), (0.26, 0.0, 0.0), ['),
    ])

    # ---- Turtle: widen and lower the shell, lift the head and neck.
    s = patch(s, [
        ('BoxSpec("dome", -4.6, -2.4, -5.2, 9.2, 3.6, 10.4, "crystal"),',
         'BoxSpec("dome", -5.0, -2.0, -5.6, 10.0, 2.8, 11.2, "crystal"),'),
        ('BoxSpec("rim", -5.2, 0.8, -5.8, 10.4, 1.4, 11.6, "crystal", grow=0.04),',
         'BoxSpec("rim", -5.6, 0.7, -6.2, 11.2, 1.2, 12.4, "crystal", grow=0.04),'),
        ('head = PartSpec("head", (0.0, 16.0, -6.2), boxes=[\n'
         '        BoxSpec("skull", -2.0, -2.4, -3.6, 4.0, 4.8, 4.8, "fur"),',
         'head = PartSpec("head", (0.0, 14.6, -7.4), boxes=[\n'
         '        BoxSpec("skull", -2.1, -2.6, -3.8, 4.2, 5.2, 5.2, "fur"),'),
        ('head.children.append(PartSpec("neck", (0.0, 1.6, 1.4), boxes=[\n'
         '        BoxSpec("bridge", -2.4, -2.0, 0.0, 4.8, 4.0, 3.6, "fur"),\n'
         '    ]))',
         'head.children.append(PartSpec("neck", (0.0, 2.0, 1.6), boxes=[\n'
         '        BoxSpec("bridge", -2.6, -2.2, 0.0, 5.2, 4.4, 4.2, "fur"),\n'
         '    ]))'),
    ])

    # ---- Goat: make the horns curl by adding pitch per segment.
    s = patch(s, [
        ('horn = PartSpec(name, (2.0 * side, -2.6, -0.2), (-0.42, 0.0, -0.52 * side), [',
         'horn = PartSpec(name, (2.0 * side, -2.6, -0.2), (-0.75, 0.0, -0.34 * side), ['),
        ('mid = PartSpec("mid", (0.0, -3.0, 0.0), (-0.52, 0.0, -0.40 * side), [',
         'mid = PartSpec("mid", (0.0, -3.0, 0.0), (-0.85, 0.0, -0.30 * side), ['),
        ('mid.children.append(PartSpec("tip", (0.0, -2.4, 0.0), (-0.5, 0.0, -0.36 * side), [',
         'mid.children.append(PartSpec("tip", (0.0, -2.4, 0.0), (-0.9, 0.0, -0.26 * side), ['),
    ])

    # ---- Crane: tuck the wings closer to the body and shorten the legs.
    s = patch(s, [
        ('wing = PartSpec(name, (3.2 * side, 8.4, 0.0), (0.0, 0.0, -0.16 * side), [\n'
         '            BoxSpec("arm", -0.9 if side > 0 else -0.9, -2.4, -3.6, 1.8, 5.4, 7.6, "feather"),',
         'wing = PartSpec(name, (2.9 * side, 8.4, 0.0), (0.0, 0.0, -0.42 * side), [\n'
         '            BoxSpec("arm", -0.9, -2.4, -3.2, 1.8, 5.4, 6.8, "feather"),'),
        ('leg = PartSpec(name, (1.5 * side, 13.2, 0.2), boxes=[\n'
         '            BoxSpec("shin", -0.6, 0.0, -0.6, 1.2, 9.2, 1.2, "metal"),',
         'leg = PartSpec(name, (1.5 * side, 14.6, 0.2), boxes=[\n'
         '            BoxSpec("shin", -0.7, 0.0, -0.7, 1.4, 7.8, 1.4, "metal"),'),
        ('leg.children.append(PartSpec("foot", (0.0, 9.0, 0.0), boxes=[',
         'leg.children.append(PartSpec("foot", (0.0, 7.6, 0.0), boxes=['),
    ])

    io.open(SPEC, 'w', encoding='utf-8', newline='\n').write(s)
    print('spec patched')


if __name__ == '__main__':
    main()
