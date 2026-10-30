"""Beast model specs: one source of truth for geometry, UVs and textures.

Proportion rules applied here (learned from the previous render pass):

* Torso depth stays near 1.3x its height; longer than that reads as a sausage.
* The head is at least 0.85x the torso height, and its top sits above the torso top so the
  silhouette shows a neck line.
* Ears are small wedges on the skull, never wide splayed plates.
* Tails taper through three segments instead of one big slab.
* Legs are 2-3 units thick and reach y=24 (the entity's feet).
"""

from __future__ import annotations

from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parent))

from uv_layout import BoxSpec, PartSpec  # noqa: E402

GROUND = 24.0


def fox():
    body = PartSpec("body", (0.0, 13.5, 0.0), boxes=[
        BoxSpec("torso", -3.2, -3.0, -4.5, 6.4, 6.0, 9.0, "fur"),
        BoxSpec("spine", -2.7, -3.5, -3.9, 5.4, 0.8, 7.8, "fur", grow=0.04),
        BoxSpec("brisket", -2.8, 1.0, -4.4, 5.6, 2.2, 3.4, "fur", grow=0.03),
        BoxSpec("haunch", -3.4, -1.8, 2.4, 6.8, 5.2, 3.0, "fur", grow=0.04),
    ])

    head = PartSpec("head", (0.0, 9.6, -6.0), boxes=[
        BoxSpec("skull", -3.0, -2.6, -3.4, 6.0, 5.2, 6.6, "fur"),
    ])
    head.children.append(PartSpec("muzzle", (0.0, 0.7, -3.2), boxes=[
        BoxSpec("bridge", -1.7, -1.0, -2.6, 3.4, 2.4, 2.6, "fur"),
        BoxSpec("nose", -0.8, 0.1, -3.2, 1.6, 1.2, 0.8, "metal"),
    ]))
    head.children.append(PartSpec("jaw", (0.0, 2.0, -2.6), boxes=[
        BoxSpec("chin", -1.5, 0.0, -2.2, 3.0, 0.9, 2.2, "fur"),
    ]))
    # Ears: small upright wedges close to the skull, tilted slightly outward.
    head.children.append(PartSpec("left_ear", (1.6, -2.4, 0.1), (-0.12, 0.0, 0.14), [
        BoxSpec("outer", -0.9, -2.6, -0.7, 1.8, 2.6, 1.4, "fur"),
        BoxSpec("inner", -0.5, -2.0, -0.8, 1.0, 1.8, 0.7, "crystal"),
    ]))
    head.children.append(PartSpec("right_ear", (-1.6, -2.4, 0.1), (-0.12, 0.0, -0.14), [
        BoxSpec("outer", -0.9, -2.6, -0.7, 1.8, 2.6, 1.4, "fur"),
        BoxSpec("inner", -0.5, -2.0, -0.8, 1.0, 1.8, 0.7, "crystal"),
    ]))
    head.children.append(PartSpec("crest", (0.0, -2.2, -0.9), boxes=[
        BoxSpec("tuft", -0.7, -1.8, -0.7, 1.4, 1.8, 1.4, "crystal"),
    ]))

    neck = PartSpec("neck", (0.0, 11.0, -4.4), (0.34, 0.0, 0.0), [
        BoxSpec("bridge", -2.3, -2.2, -1.8, 4.6, 4.4, 4.0, "fur"),
    ])
    parts = [body, neck, head]
    for name, x, z in (("front_right", -2.0, -2.8), ("front_left", 2.0, -2.8),
                       ("back_right", -2.0, 2.8), ("back_left", 2.0, 2.8)):
        parts.append(PartSpec(name, (x, 16.5, z), boxes=[
            BoxSpec("upper", -1.1, 0.0, -1.1, 2.2, 6.0, 2.2, "fur"),
            BoxSpec("paw", -1.3, 5.8, -1.6, 2.6, 1.5, 2.9, "fur"),
        ]))

    tail = PartSpec("tail", (0.0, 12.2, 4.2), (0.48, 0.0, 0.0), [
        BoxSpec("base", -1.9, -1.9, 0.0, 3.8, 3.8, 4.6, "fur"),
    ])
    mid = PartSpec("tail_mid", (0.0, 0.0, 4.2), (0.14, 0.0, 0.0), [
        BoxSpec("mid", -2.6, -2.6, 0.0, 5.2, 5.2, 4.4, "fur"),
    ])
    mid.children.append(PartSpec("tail_tip", (0.0, 0.0, 4.0), (0.12, 0.0, 0.0), [
        BoxSpec("tip", -1.9, -1.9, 0.0, 3.8, 3.8, 4.0, "crystal"),
    ]))
    tail.children.append(mid)
    parts.append(tail)

    return 128, parts, (146, 154, 170), (96, 226, 214)


def wolf():
    body = PartSpec("body", (0.0, 13.0, 0.0), boxes=[
        BoxSpec("torso", -3.8, -3.4, -5.0, 7.6, 6.8, 10.0, "fur"),
        BoxSpec("spine", -3.2, -4.0, -4.3, 6.4, 0.9, 8.6, "fur", grow=0.05),
        BoxSpec("brisket", -3.0, 1.2, -4.8, 6.0, 2.4, 3.8, "fur", grow=0.03),
        BoxSpec("haunch", -4.0, -2.2, 2.6, 8.0, 5.8, 3.4, "fur", grow=0.05),
    ])

    mane = PartSpec("mane", (0.0, 13.0, -4.6), boxes=[
        BoxSpec("ruff", -4.3, -4.3, -2.2, 8.6, 9.0, 5.0, "fur", grow=0.07),
    ])
    mane.children.append(PartSpec("mane_top", (0.0, -3.9, -0.6), (-0.2, 0.0, 0.0), [
        BoxSpec("ridge", -1.4, -1.3, 0.0, 2.8, 2.6, 7.0, "fur"),
    ]))
    mane.children.append(PartSpec("mane_left", (-3.4, -0.8, 0.0), (-0.08, 0.16, -0.26), [
        BoxSpec("flank", -1.7, -1.8, 0.0, 1.7, 3.6, 6.4, "fur"),
    ]))
    mane.children.append(PartSpec("mane_right", (3.4, -0.8, 0.0), (-0.08, -0.16, 0.26), [
        BoxSpec("flank", 0.0, -1.8, 0.0, 1.7, 3.6, 6.4, "fur"),
    ]))

    head = PartSpec("head", (0.0, 9.0, -7.2), boxes=[
        BoxSpec("skull", -3.2, -3.0, -3.6, 6.4, 6.0, 7.0, "fur"),
    ])
    head.children.append(PartSpec("snout", (0.0, 0.6, -3.4), boxes=[
        BoxSpec("muzzle", -1.9, -1.0, -3.2, 3.8, 3.0, 3.2, "fur"),
        BoxSpec("nose", -0.9, 0.0, -3.9, 1.8, 1.3, 0.8, "metal"),
    ]))
    head.children.append(PartSpec("left_ear", (1.8, -2.8, 0.3), (-0.14, 0.0, 0.16), [
        BoxSpec("outer", -0.9, -3.2, -0.7, 1.8, 3.2, 1.5, "fur"),
    ]))
    head.children.append(PartSpec("right_ear", (-1.8, -2.8, 0.3), (-0.14, 0.0, -0.16), [
        BoxSpec("outer", -0.9, -3.2, -0.7, 1.8, 3.2, 1.5, "fur"),
    ]))

    collar = PartSpec("collar", (0.0, 13.0, -4.6), boxes=[
        BoxSpec("band", -4.4, -1.0, -3.3, 8.8, 2.0, 6.6, "metal", grow=0.05),
    ])
    collar.children.append(PartSpec("pendant", (0.0, 1.0, -3.8), boxes=[
        BoxSpec("gem", -1.4, -0.4, -0.4, 2.8, 3.6, 0.9, "crystal"),
    ]))

    neck = PartSpec("neck", (0.0, 10.8, -5.0), (0.36, 0.0, 0.0), [
        BoxSpec("bridge", -2.8, -2.6, -2.0, 5.6, 5.2, 4.6, "fur"),
    ])
    parts = [body, neck, mane, head, collar]
    for name, x, z in (("front_right", -2.4, -3.2), ("front_left", 2.4, -3.2),
                       ("back_right", -2.4, 3.4), ("back_left", 2.4, 3.4)):
        parts.append(PartSpec(name, (x, 16.4, z), boxes=[
            BoxSpec("upper", -1.3, 0.0, -1.3, 2.6, 6.0, 2.6, "fur"),
            BoxSpec("paw", -1.5, 5.8, -1.8, 3.0, 1.6, 3.3, "fur"),
        ]))

    tail = PartSpec("tail", (0.0, 10.4, 5.0), (0.26, 0.0, 0.0), [
        BoxSpec("base", -1.9, -1.9, 0.0, 3.8, 3.8, 5.2, "fur"),
    ])
    mid = PartSpec("tail_mid", (0.0, 0.0, 4.8), (0.14, 0.0, 0.0), [
        BoxSpec("mid", -2.3, -2.3, 0.0, 4.6, 4.6, 4.8, "fur"),
    ])
    mid.children.append(PartSpec("tail_tip", (0.0, 0.0, 4.4), (0.12, 0.0, 0.0), [
        BoxSpec("tip", -2.0, -2.0, 0.0, 4.0, 4.0, 4.4, "crystal"),
    ]))
    tail.children.append(mid)
    parts.append(tail)

    return 128, parts, (108, 124, 140), (110, 228, 250)


def goat():
    body = PartSpec("body", (0.0, 13.5, 0.0), boxes=[
        BoxSpec("torso", -3.8, -3.6, -4.8, 7.6, 7.2, 9.6, "fur"),
        BoxSpec("back", -3.2, -4.1, -4.0, 6.4, 0.8, 8.0, "fur", grow=0.04),
    ])
    armor = PartSpec("armor", (0.0, 13.5, 0.0), boxes=[
        BoxSpec("plate", -4.3, -2.2, -4.2, 8.6, 2.6, 8.4, "metal", grow=0.04),
        BoxSpec("saddle", -2.8, -2.9, -1.6, 5.6, 0.9, 3.2, "fur"),
    ])

    head = PartSpec("head", (0.0, 9.2, -5.6), boxes=[
        BoxSpec("skull", -2.9, -2.9, -3.2, 5.8, 5.8, 6.4, "fur"),
    ])
    head.children.append(PartSpec("muzzle", (0.0, 1.0, -3.2), boxes=[
        BoxSpec("muzzle", -2.0, -1.2, -2.8, 4.0, 3.2, 2.8, "fur"),
        BoxSpec("nose", -1.0, 0.2, -3.5, 2.0, 1.4, 0.9, "metal"),
    ]))
    head.children.append(PartSpec("beard", (0.0, 2.2, -2.2), (0.26, 0.0, 0.0), [
        BoxSpec("beard", -1.2, 0.0, -0.9, 2.4, 4.0, 1.8, "fur"),
    ]))

    # Ram horns: three segments curling back and out, each smaller than the last.
    # Horns sweep backward in three flat segments. The segments are siblings under the head
    # rather than nested, because nesting compounds each rotation and previously pushed the
    # tip above the animal's back.
    for name, side in (("left_horn", 1.0), ("right_horn", -1.0)):
        head.children.append(PartSpec(f"{name}_base", (1.9 * side, -2.2, 0.0),
                (-1.45, 0.0, -0.26 * side), [
                    BoxSpec("base", -1.0, -2.6, -1.0, 2.0, 2.6, 2.0, "metal"),
                ]))
        head.children.append(PartSpec(f"{name}_mid", (2.4 * side, -3.6, 1.6),
                (-1.55, 0.0, -0.18 * side), [
                    BoxSpec("mid", -0.8, -2.2, -0.8, 1.6, 2.2, 1.6, "metal"),
                ]))
        head.children.append(PartSpec(f"{name}_tip", (2.7 * side, -4.4, 3.2),
                (-1.60, 0.0, -0.12 * side), [
                    BoxSpec("tip", -0.6, -1.7, -0.6, 1.2, 1.7, 1.2, "metal"),
                ]))

    parts = [body, armor, head]
    for name, x, z in (("front_right_leg", -2.9, -3.4), ("front_left_leg", 2.9, -3.4),
                       ("back_right_leg", -2.9, 3.4), ("back_left_leg", 2.9, 3.4)):
        parts.append(PartSpec(name, (x, 16.4, z), boxes=[
            BoxSpec("upper", -1.5, 0.0, -1.5, 3.0, 6.2, 3.0, "fur"),
            BoxSpec("hoof", -1.7, 6.0, -1.8, 3.4, 1.6, 3.6, "metal"),
        ]))

    tail = PartSpec("tail", (0.0, 11.6, 4.6), (0.7, 0.0, 0.0), [
        BoxSpec("tail", -1.2, -1.2, 0.0, 2.4, 2.4, 3.6, "fur"),
    ])
    parts.append(tail)

    return 128, parts, (118, 110, 98), (244, 164, 62)


def turtle():
    # A turtle must read from the side: shallow dome, head above the rim, splayed flippers.
    body = PartSpec("body", (0.0, 18.5, 0.0), boxes=[
        BoxSpec("belly", -4.2, 0.0, -4.8, 8.4, 2.2, 9.6, "fur"),
    ])
    shell = PartSpec("shell", (0.0, 15.4, 0.0), boxes=[
        BoxSpec("dome", -5.0, -2.0, -5.6, 10.0, 2.8, 11.2, "crystal"),
        BoxSpec("rim", -5.6, 0.7, -6.2, 11.2, 1.2, 12.4, "crystal", grow=0.04),
    ])
    crystals = PartSpec("crystals", (0.0, -2.2, 0.0), boxes=[
        BoxSpec("main", -1.1, -3.8, -1.1, 2.2, 3.8, 2.2, "crystal"),
    ])
    crystals.children.append(PartSpec("left_crystal", (-2.2, 0.4, 1.0), (-0.2, 0.0, -0.42), [
        BoxSpec("shard", -0.7, -2.4, -0.7, 1.4, 2.4, 1.4, "crystal"),
    ]))
    crystals.children.append(PartSpec("rear_crystal", (2.0, 0.6, 2.4), (0.16, 0.0, 0.36), [
        BoxSpec("shard", -0.7, -2.0, -0.7, 1.4, 2.0, 1.4, "crystal"),
    ]))
    shell.children.append(crystals)

    # Head: top above the shell, projecting forward, with a neck bridging the gap.
    head = PartSpec("head", (0.0, 14.6, -7.4), boxes=[
        BoxSpec("skull", -2.1, -2.6, -3.8, 4.2, 5.2, 5.2, "fur"),
        BoxSpec("beak", -1.0, -0.9, -4.6, 2.0, 1.8, 1.2, "metal"),
        BoxSpec("crest", -0.6, -3.6, -1.2, 1.2, 1.4, 2.0, "crystal"),
    ])
    head.children.append(PartSpec("neck", (0.0, 2.0, 1.6), boxes=[
        BoxSpec("bridge", -2.6, -2.2, 0.0, 5.2, 4.4, 4.2, "fur"),
    ]))

    parts = [body, shell, head]
    for name, x, z in (("front_right", -4.8, -3.2), ("front_left", 4.8, -3.2),
                       ("back_right", -4.8, 3.2), ("back_left", 4.8, 3.2)):
        parts.append(PartSpec(name, (x, 19.6, z), boxes=[
            BoxSpec("flipper", -2.4, -1.2, -2.2, 4.8, 2.6, 4.4, "fur"),
        ]))

    tail = PartSpec("tail", (0.0, 19.4, 5.6), boxes=[
        BoxSpec("stub", -0.9, -0.8, 0.0, 1.8, 1.6, 2.8, "fur"),
    ])
    parts.append(tail)

    return 128, parts, (44, 92, 78), (88, 232, 224)


def crane():
    body = PartSpec("body", (0.0, 10.5, 0.0), boxes=[
        BoxSpec("torso", -3.4, -3.4, -4.2, 6.8, 6.8, 9.0, "feather"),
        BoxSpec("breast", -3.0, 2.2, -3.8, 6.0, 2.0, 4.4, "feather", grow=0.04),
    ])

    # S-curve neck: lower segment leans forward, upper segment leans back.
    neck = PartSpec("neck", (0.0, 7.4, -3.4), (-0.34, 0.0, 0.0), [
        BoxSpec("lower", -1.4, -5.4, -1.4, 2.8, 5.4, 2.8, "feather"),
    ])
    upper = PartSpec("upper", (0.0, -5.0, 0.0), (0.62, 0.0, 0.0), [
        BoxSpec("upper", -1.3, -4.2, -1.3, 2.6, 4.2, 2.6, "feather"),
    ])
    head = PartSpec("head", (0.0, -3.8, 0.0), boxes=[
        BoxSpec("skull", -1.8, -1.8, -2.4, 3.6, 3.6, 4.4, "feather"),
        BoxSpec("crest", -0.5, -3.0, -0.8, 1.0, 1.4, 2.2, "metal"),
        BoxSpec("beak", -0.9, -0.7, -4.4, 1.8, 1.6, 2.2, "metal"),
    ])
    upper.children.append(head)
    neck.children.append(upper)

    wings = []
    for name, side in (("left_wing", 1.0), ("right_wing", -1.0)):
        wing = PartSpec(name, (2.9 * side, 8.4, 0.0), (0.0, 0.0, -0.42 * side), [
            BoxSpec("arm", -0.9, -2.4, -3.2, 1.8, 5.4, 6.8, "feather"),
        ])
        for i in range(3):
            wing.children.append(PartSpec(
                f"feather_{i}", (0.9 * side, 1.4 + i * 1.2, -2.2 + i * 1.6),
                (0.06 + i * 0.09, 0.0, -0.1 * side), [
                    BoxSpec("vane", -0.5, -0.9, -0.9, 1.0, 2.4, 7.4, "feather"),
                ]))
        wings.append(wing)

    legs = []
    for name, side in (("left_leg", 1.0), ("right_leg", -1.0)):
        leg = PartSpec(name, (1.5 * side, 14.6, 0.2), boxes=[
            BoxSpec("shin", -0.7, 0.0, -0.7, 1.4, 7.8, 1.4, "metal"),
        ])
        leg.children.append(PartSpec("foot", (0.0, 7.6, 0.0), boxes=[
            BoxSpec("foot", -0.9, 0.0, -2.6, 1.8, 1.0, 3.4, "metal"),
        ]))
        legs.append(leg)

    tail = PartSpec("tail", (0.0, 10.0, 4.4), (0.38, 0.0, 0.0), [
        BoxSpec("base", -1.4, -1.0, 0.0, 2.8, 2.0, 6.4, "feather"),
    ])
    tail.children.append(PartSpec("left_tail_feather", (1.2, 0.1, 0.4), (0.06, 0.2, 0.0), [
        BoxSpec("vane", -0.9, -0.9, 0.0, 1.8, 1.8, 6.6, "feather"),
    ]))
    tail.children.append(PartSpec("right_tail_feather", (-1.2, 0.1, 0.4), (0.06, -0.2, 0.0), [
        BoxSpec("vane", -0.9, -0.9, 0.0, 1.8, 1.8, 6.6, "feather"),
    ]))

    return 128, [body, neck, *wings, *legs, tail], (216, 204, 172), (214, 92, 48)
