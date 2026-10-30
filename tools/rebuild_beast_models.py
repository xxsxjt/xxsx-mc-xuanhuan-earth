"""Rebuild the spirit beast textures with an explicit UV layout.

Each creature is described once as parts and boxes; the allocator assigns UVs and paints
the texture, and the same spec is used to print the offsets for the Java model. This keeps
model geometry and texture from drifting apart.
"""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from uv_layout import BoxSpec, PartSpec, build  # noqa: E402


def fox() -> tuple[int, list[PartSpec], tuple[int, int, int], tuple[int, int, int]]:
    # Proportions: a fox is roughly 3.5 torso-lengths long including the tail, with a head
    # about 40% of the torso length and legs that reach the ground with visible clearance.
    body = PartSpec("body", (0.0, 12.4, 0.0), boxes=[
        BoxSpec("torso", -3.4, -3.4, -5.0, 6.8, 6.8, 10.0, "fur"),
        BoxSpec("ridge", -2.8, -4.0, -4.2, 5.6, 1.0, 8.4, "fur", grow=0.05),
        BoxSpec("belly", -3.0, 3.0, -4.0, 6.0, 1.0, 8.0, "fur", grow=0.04),
    ])
    head = PartSpec("head", (0.0, 10.2, -6.4), boxes=[
        BoxSpec("skull", -3.0, -3.0, -3.0, 6.0, 6.0, 6.0, "fur"),
    ])
    head.children.append(PartSpec("muzzle", (0.0, 0.6, -3.4), boxes=[
        BoxSpec("bridge", -1.8, -1.0, -3.4, 3.6, 2.8, 3.4, "fur"),
        BoxSpec("nose", -0.9, 0.2, -4.4, 1.8, 1.4, 1.0, "metal"),
    ]))
    head.children.append(PartSpec("jaw", (0.0, 2.2, -3.0), boxes=[
        BoxSpec("chin", -1.6, 0.0, -2.6, 3.2, 1.0, 2.6, "fur"),
    ]))
    # Ears sit on the skull, not floating beside it.
    head.children.append(PartSpec("left_ear", (1.9, -3.0, -0.6), (-0.10, 0.0, 0.14), [
        BoxSpec("outer", -1.3, -4.0, -0.9, 2.6, 4.0, 1.8, "fur"),
        BoxSpec("inner", -0.8, -3.2, -1.1, 1.6, 2.8, 1.0, "crystal"),
    ]))
    head.children.append(PartSpec("right_ear", (-1.9, -3.0, -0.6), (-0.10, 0.0, -0.14), [
        BoxSpec("outer", -1.3, -4.0, -0.9, 2.6, 4.0, 1.8, "fur"),
        BoxSpec("inner", -0.8, -3.2, -1.1, 1.6, 2.8, 1.0, "crystal"),
    ]))
    head.children.append(PartSpec("crest", (0.0, -3.2, -1.6), boxes=[
        BoxSpec("tuft", -0.9, -3.6, -0.9, 1.8, 3.6, 1.8, "crystal"),
    ]))

    neck = PartSpec("neck", (0.0, 11.6, -4.4), (0.32, 0.0, 0.0), [
        BoxSpec("bridge", -2.4, -2.4, -2.2, 4.8, 4.8, 4.4, "fur"),
    ])
    parts = [body, neck, head]
    # Legs reach from the torso underside (y=17.6) to the ground (y=24).
    for name, x, z in (("front_right", -2.2, -3.4), ("front_left", 2.2, -3.4),
                       ("back_right", -2.2, 3.4), ("back_left", 2.2, 3.4)):
        parts.append(PartSpec(name, (x, 16.0, z), boxes=[
            BoxSpec("upper", -1.1, 0.0, -1.1, 2.2, 6.0, 2.2, "fur"),
            BoxSpec("paw", -1.3, 5.8, -1.8, 2.6, 1.4, 3.0, "fur"),
        ]))

    tail = PartSpec("tail", (0.0, 11.4, 4.6), (0.30, 0.0, 0.0), [
        BoxSpec("base", -2.4, -2.4, 0.0, 4.8, 4.8, 6.0, "fur"),
    ])
    mid = PartSpec("tail_mid", (0.0, 0.0, 5.6), (0.10, 0.0, 0.0), [
        BoxSpec("mid", -2.8, -2.8, 0.0, 5.6, 5.6, 5.4, "fur"),
    ])
    mid.children.append(PartSpec("tail_tip", (0.0, 0.0, 5.0), (0.10, 0.0, 0.0), [
        BoxSpec("tip", -2.2, -2.2, 0.0, 4.4, 4.4, 4.6, "crystal"),
    ]))
    tail.children.append(mid)
    parts.append(tail)

    return 128, parts, (196, 200, 206), (96, 226, 214)


def wolf() -> tuple[int, list[PartSpec], tuple[int, int, int], tuple[int, int, int]]:
    body = PartSpec("body", (0.0, 14.0, 0.0), boxes=[
        BoxSpec("torso", -4.0, -4.0, -7.0, 8.0, 8.0, 14.0, "fur"),
        BoxSpec("spine", -3.4, -4.8, -6.2, 6.8, 1.0, 12.4, "fur", grow=0.05),
        BoxSpec("chest", -3.6, 2.6, -6.6, 7.2, 2.0, 5.0, "fur", grow=0.06),
    ])
    mane = PartSpec("mane", (0.0, 14.0, -5.5), boxes=[
        BoxSpec("ruff", -4.6, -5.2, -3.0, 9.2, 10.4, 6.0, "fur", grow=0.1),
    ])
    mane.children.append(PartSpec("mane_top", (0.0, -4.7, -0.5), (-0.18, 0.0, 0.0), [
        BoxSpec("ridge", -1.5, -1.5, 0.0, 3.0, 3.0, 9.0, "fur"),
    ]))
    mane.children.append(PartSpec("mane_left", (-3.7, -1.0, 0.0), (-0.1, 0.18, -0.28), [
        BoxSpec("flank", -2.0, -2.0, 0.0, 2.0, 4.0, 8.0, "fur"),
    ]))
    mane.children.append(PartSpec("mane_right", (3.7, -1.0, 0.0), (-0.1, -0.18, 0.28), [
        BoxSpec("flank", 0.0, -2.0, 0.0, 2.0, 4.0, 8.0, "fur"),
    ]))

    head = PartSpec("head", (0.0, 11.5, -7.0), boxes=[
        BoxSpec("skull", -3.5, -3.5, -4.5, 7.0, 7.0, 7.0, "fur"),
    ])
    head.children.append(PartSpec("snout", (0.0, 0.0, -4.5), boxes=[
        BoxSpec("muzzle", -2.0, -0.6, -4.0, 4.0, 3.2, 4.0, "fur"),
        BoxSpec("nose", -1.0, 0.2, -5.0, 2.0, 1.4, 1.0, "metal"),
    ]))
    head.children.append(PartSpec("left_ear", (2.1, -3.2, -1.4), (-0.1, 0.0, 0.16), [
        BoxSpec("outer", -1.0, -5.2, -1.0, 2.0, 5.2, 2.0, "fur"),
    ]))
    head.children.append(PartSpec("right_ear", (-2.1, -3.2, -1.4), (-0.1, 0.0, -0.16), [
        BoxSpec("outer", -1.0, -5.2, -1.0, 2.0, 5.2, 2.0, "fur"),
    ]))

    collar = PartSpec("collar", (0.0, 14.0, -5.5), boxes=[
        BoxSpec("band", -4.6, -1.0, -3.6, 9.2, 2.0, 7.2, "metal", grow=0.06),
    ])
    collar.children.append(PartSpec("pendant", (0.0, 1.0, -4.0), boxes=[
        BoxSpec("gem", -1.5, -0.5, -0.5, 3.0, 4.0, 1.0, "crystal"),
    ]))

    parts = [body, mane, head, collar]
    for name, x, z in (("front_right", -2.7, -4.7), ("front_left", 2.7, -4.7),
                       ("back_right", -2.7, 4.7), ("back_left", 2.7, 4.7)):
        parts.append(PartSpec(name, (x, 16.5, z), boxes=[
            BoxSpec("upper", -1.2, -1.0, -1.2, 2.4, 4.2, 2.4, "fur"),
            BoxSpec("paw", -1.6, 3.2, -2.4, 3.2, 1.6, 4.0, "fur"),
        ]))

    tail = PartSpec("tail", (0.0, 13.5, 6.3), (0.45, 0.0, 0.0), [
        BoxSpec("base", -2.0, -2.0, 0.0, 4.0, 4.0, 7.0, "fur"),
    ])
    mid = PartSpec("tail_mid", (0.0, 0.0, 6.2), (0.08, 0.0, 0.0), [
        BoxSpec("mid", -2.6, -2.6, 0.0, 5.2, 5.2, 7.0, "fur"),
    ])
    mid.children.append(PartSpec("tail_tip", (0.0, 0.0, 6.2), (0.12, 0.0, 0.0), [
        BoxSpec("tip", -3.0, -3.0, 0.0, 6.0, 6.0, 7.0, "crystal"),
    ]))
    tail.children.append(mid)
    parts.append(tail)

    return 128, parts, (108, 124, 140), (110, 228, 250)


def goat() -> tuple[int, list[PartSpec], tuple[int, int, int], tuple[int, int, int]]:
    body = PartSpec("body", (0.0, 13.0, 0.0), boxes=[
        BoxSpec("torso", -5.0, -5.0, -7.0, 10.0, 10.0, 14.0, "fur"),
        BoxSpec("back", -4.4, -5.6, -6.4, 8.8, 1.0, 12.8, "fur", grow=0.04),
    ])
    armor = PartSpec("armor", (0.0, 13.0, 0.0), boxes=[
        BoxSpec("plate", -5.6, -2.4, -5.0, 11.2, 3.0, 10.0, "metal", grow=0.05),
        BoxSpec("saddle", -3.6, -3.0, -2.0, 7.2, 1.0, 4.0, "fur"),
    ])

    head = PartSpec("head", (0.0, 8.0, -7.0), boxes=[
        BoxSpec("skull", -3.6, -3.6, -4.4, 7.2, 7.2, 8.0, "fur"),
    ])
    head.children.append(PartSpec("muzzle", (0.0, 1.2, -4.4), boxes=[
        BoxSpec("muzzle", -2.2, -1.2, -3.0, 4.4, 3.6, 3.0, "fur"),
        BoxSpec("nose", -1.1, 0.4, -4.0, 2.2, 1.6, 1.0, "metal"),
    ]))
    head.children.append(PartSpec("beard", (0.0, 2.6, -3.0), (0.2, 0.0, 0.0), [
        BoxSpec("beard", -1.4, 0.0, -1.0, 2.8, 5.0, 2.0, "fur"),
    ]))

    # Ram-style horns: three segments sweeping back and out, each shorter than the last.
    for name, side in (("left_horn", 1.0), ("right_horn", -1.0)):
        horn = PartSpec(name, (2.2 * side, -2.6, -0.4), (-0.35, 0.0, -0.55 * side), [
            BoxSpec("base", -1.3, -4.4, -1.3, 2.6, 4.4, 2.6, "metal"),
        ])
        mid = PartSpec("mid", (0.0, -4.0, 0.0), (-0.55, 0.0, -0.42 * side), [
            BoxSpec("mid", -1.1, -3.6, -1.1, 2.2, 3.6, 2.2, "metal"),
        ])
        mid.children.append(PartSpec("tip", (0.0, -3.2, 0.0), (-0.55, 0.0, -0.38 * side), [
            BoxSpec("tip", -0.8, -2.8, -0.8, 1.6, 2.8, 1.6, "metal"),
        ]))
        horn.children.append(mid)
        head.children.append(horn)

    parts = [body, armor, head]
    for name, x, z in (("front_right_leg", -4.0, -5.5), ("front_left_leg", 4.0, -5.5),
                       ("back_right_leg", -4.0, 5.5), ("back_left_leg", 4.0, 5.5)):
        parts.append(PartSpec(name, (x, 14.0, z), boxes=[
            BoxSpec("upper", -2.0, 0.0, -2.0, 4.0, 6.0, 4.0, "fur"),
            BoxSpec("hoof", -2.2, 6.0, -2.4, 4.4, 1.6, 4.8, "metal"),
        ]))

    tail = PartSpec("tail", (0.0, 12.0, 7.0), (0.6, 0.0, 0.0), [
        BoxSpec("tail", -1.6, -1.6, 0.0, 3.2, 3.2, 5.0, "fur"),
    ])
    parts.append(tail)

    return 128, parts, (118, 110, 98), (244, 164, 62)


def turtle() -> tuple[int, list[PartSpec], tuple[int, int, int], tuple[int, int, int]]:
    # Turtle silhouette rules: a shallow dome narrower than the limb span, a head that
    # clearly projects forward and up, and four splayed flippers visible from the side.
    body = PartSpec("body", (0.0, 19.0, 0.0), boxes=[
        BoxSpec("belly", -4.6, 0.0, -4.6, 9.2, 2.4, 10.2, "fur"),
    ])
    shell = PartSpec("shell", (0.0, 16.6, 0.0), boxes=[
        BoxSpec("dome", -5.0, -3.0, -5.0, 10.0, 3.6, 11.0, "crystal"),
        BoxSpec("rim", -5.6, 0.4, -5.6, 11.2, 1.4, 12.2, "crystal", grow=0.04),
    ])
    crystals = PartSpec("crystals", (0.0, -2.8, 0.0), boxes=[
        BoxSpec("main", -1.2, -5.0, -1.2, 2.4, 5.0, 2.4, "crystal"),
    ])
    crystals.children.append(PartSpec("left_crystal", (-2.4, 0.4, 1.2), (-0.2, 0.0, -0.42), [
        BoxSpec("shard", -0.7, -3.0, -0.7, 1.4, 3.0, 1.4, "crystal"),
    ]))
    crystals.children.append(PartSpec("rear_crystal", (2.2, 0.6, 2.8), (0.16, 0.0, 0.36), [
        BoxSpec("shard", -0.7, -2.4, -0.7, 1.4, 2.4, 1.4, "crystal"),
    ]))
    shell.children.append(crystals)

    # Head: projects forward past the rim, with a neck block bridging it to the shell so
    # the head never reads as a detached cube.
    head = PartSpec("head", (0.0, 17.2, -6.6), boxes=[
        BoxSpec("skull", -2.2, -2.8, -4.2, 4.4, 5.6, 5.8, "fur"),
        BoxSpec("beak", -1.2, -1.0, -5.4, 2.4, 2.2, 1.6, "metal"),
        BoxSpec("crest", -0.7, -4.2, -1.8, 1.4, 1.8, 2.6, "crystal"),
    ])
    head.children.append(PartSpec("neck", (0.0, 1.2, 1.0), boxes=[
        BoxSpec("bridge", -2.8, -2.2, 0.0, 5.6, 4.4, 4.2, "fur"),
    ]))

    parts = [body, shell, head]
    for name, x, z in (("front_right", -5.4, -3.6), ("front_left", 5.4, -3.6),
                       ("back_right", -5.4, 3.6), ("back_left", 5.4, 3.6)):
        parts.append(PartSpec(name, (x, 20.4, z), boxes=[
            BoxSpec("flipper", -2.8, -1.4, -2.4, 5.6, 2.8, 4.8, "fur"),
        ]))

    tail = PartSpec("tail", (0.0, 20.0, 6.2), boxes=[
        BoxSpec("stub", -0.9, -0.8, 0.0, 1.8, 1.6, 3.0, "fur"),
    ])
    parts.append(tail)

    return 128, parts, (56, 108, 92), (96, 240, 232)


def crane() -> tuple[int, list[PartSpec], tuple[int, int, int], tuple[int, int, int]]:
    body = PartSpec("body", (0.0, 11.0, 0.0), boxes=[
        BoxSpec("torso", -4.0, -4.0, -5.0, 8.0, 8.0, 11.0, "feather"),
        BoxSpec("chest", -3.4, 2.4, -4.6, 6.8, 2.0, 5.0, "feather", grow=0.04),
    ])
    neck = PartSpec("neck", (0.0, 8.0, -4.0), (-0.25, 0.0, 0.0), [
        BoxSpec("lower", -1.6, -7.0, -1.6, 3.2, 7.0, 3.2, "feather"),
    ])
    head = PartSpec("head", (0.0, -6.6, 0.0), boxes=[
        BoxSpec("skull", -2.0, -2.4, -2.6, 4.0, 4.4, 4.6, "feather"),
        BoxSpec("crest", -0.6, -4.0, -0.6, 1.2, 2.0, 3.0, "metal"),
        BoxSpec("beak", -1.0, -0.8, -6.0, 2.0, 1.8, 3.6, "metal"),
    ])
    neck.children.append(head)

    left_wing = PartSpec("left_wing", (3.4, 9.0, 0.0), (0.0, 0.0, -0.2), [
        BoxSpec("arm", 0.0, -2.0, -4.0, 2.0, 6.0, 10.0, "feather"),
    ])
    for i in range(3):
        left_wing.children.append(
            PartSpec(f"feather_{i}", (1.6, 1.6 + i * 1.3, -2.4 + i * 1.8),
                     (0.04 + i * 0.1, 0.0, -0.08 - i * 0.05), [
                         BoxSpec("vane", 0.0, -1.0, -1.0, 1.0, 3.0, 9.0, "feather"),
                     ]))
    right_wing = PartSpec("right_wing", (-3.4, 9.0, 0.0), (0.0, 0.0, 0.2), [
        BoxSpec("arm", -2.0, -2.0, -4.0, 2.0, 6.0, 10.0, "feather"),
    ])
    for i in range(3):
        right_wing.children.append(
            PartSpec(f"feather_{i}", (-1.6, 1.6 + i * 1.3, -2.4 + i * 1.8),
                     (0.04 + i * 0.1, 0.0, 0.08 + i * 0.05), [
                         BoxSpec("vane", -1.0, -1.0, -1.0, 1.0, 3.0, 9.0, "feather"),
                     ]))

    left_leg = PartSpec("left_leg", (1.7, 13.0, 0.0), boxes=[
        BoxSpec("shin", -0.6, 0.0, -0.6, 1.2, 10.0, 1.2, "metal"),
    ])
    left_leg.children.append(PartSpec("foot", (0.0, 10.0, 0.0), boxes=[
        BoxSpec("foot", -1.0, 0.0, -3.0, 2.0, 1.0, 4.0, "metal"),
    ]))
    right_leg = PartSpec("right_leg", (-1.7, 13.0, 0.0), boxes=[
        BoxSpec("shin", -0.6, 0.0, -0.6, 1.2, 10.0, 1.2, "metal"),
    ])
    right_leg.children.append(PartSpec("foot", (0.0, 10.0, 0.0), boxes=[
        BoxSpec("foot", -1.0, 0.0, -3.0, 2.0, 1.0, 4.0, "metal"),
    ]))

    tail = PartSpec("tail", (0.0, 11.0, 5.0), (0.42, 0.0, 0.0), [
        BoxSpec("base", -1.6, -1.2, 0.0, 3.2, 2.4, 9.0, "feather"),
    ])
    tail.children.append(PartSpec("left_tail_feather", (1.6, 0.2, 0.5), (0.08, 0.18, 0.0), [
        BoxSpec("vane", -1.0, -1.0, 0.0, 2.0, 2.0, 9.0, "feather"),
    ]))
    tail.children.append(PartSpec("right_tail_feather", (-1.6, 0.2, 0.5), (0.08, -0.18, 0.0), [
        BoxSpec("vane", -1.0, -1.0, 0.0, 2.0, 2.0, 9.0, "feather"),
    ]))

    return 128, [body, neck, left_wing, right_wing, left_leg, right_leg, tail], \
        (238, 228, 200), (226, 108, 62)


def main() -> None:
    for name, factory in (("spirit_fox", fox), ("wind_wolf", wolf), ("stonehorn_goat", goat),
                          ("crystal_turtle", turtle), ("ember_crane", crane)):
        size, parts, base, accent = factory()
        build(name, size, parts, base, accent)


if __name__ == "__main__":
    main()
