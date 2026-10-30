"""Render orthographic front / side / top views of a beast model.

A single three-quarter view hides most of the silhouette. These three views make it obvious
whether the head, legs and tail are actually shaped and positioned correctly.
"""

from __future__ import annotations

import math
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import preview_models as pm  # noqa: E402
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "tmp" / "model-views"

VIEWS = {
    "front": (0.0, 0.0),
    "side": (math.pi / 2, 0.0),
    "top": (0.0, -math.pi / 2 + 0.001),
    "iso": (2.4, 0.35),
}


def render_view(model, texture: Path, size: tuple[int, int], out: Path,
                yaw: float, pitch: float, scale: float = 9.0) -> None:
    tex = Image.open(texture).convert("RGBA")
    W, H = size
    canvas = Image.new("RGBA", (W, H), (16, 18, 22, 255))
    cubes = []
    pm.collect(model, [[1, 0, 0], [0, 1, 0], [0, 0, 1]], [0.0, 0.0, 0.0], cubes)

    cy, sy = math.cos(yaw), math.sin(yaw)
    cp, sp = math.cos(pitch), math.sin(pitch)

    def project(p):
        x, y, z = p
        x, z = x * cy - z * sy, x * sy + z * cy
        y, z = y * cp - z * sp, y * sp + z * cp
        return (W / 2 + x * scale, H / 2 - 30 + y * scale), z

    faces = []
    for box, m, offset, name in cubes:
        for face, corners in pm.face_quads(box).items():
            world = []
            for corner in corners:
                local = pm.apply(m, list(corner))
                world.append((local[0] + offset[0], local[1] + offset[1], local[2] + offset[2]))
            projected = [project(p) for p in world]
            # Sort by camera-space depth of the face centre, not by screen Y.
            centre = [
                sum(p[1] for p in projected) / 4.0,
                sum(w[1] for w in world) / 4.0,
            ]
            faces.append((centre[0], centre[1], face, box, projected))
    # Nearer faces (larger camera-space z) must be drawn last.
    faces.sort(key=lambda f: (f[0], f[1]))

    for _, _, face, box, projected in faces:
        points = [p[0] for p in projected]
        u0, v0, u1, v1 = pm.face_uv(box, face)
        u0, v0 = max(0, u0), max(0, v0)
        u1, v1 = min(tex.width, u1), min(tex.height, v1)
        if u1 <= u0 or v1 <= v0:
            continue
        patch = tex.crop((u0, v0, u1, v1))
        if patch.width == 0 or patch.height == 0:
            continue
        sx = [p[0] for p in points]
        sy_ = [p[1] for p in points]
        px0, py0, px1, py1, px3, py3 = sx[0], sy_[0], sx[1], sy_[1], sx[3], sy_[3]
        det = (px1 - px0) * (py3 - py0) - (px3 - px0) * (py1 - py0)
        if abs(det) < 1e-6:
            continue
        a = (px1 - px0) / patch.width
        b = (px3 - px0) / patch.height
        d = (py1 - py0) / patch.width
        e = (py3 - py0) / patch.height
        inv = 1.0 / (a * e - b * d)
        ia, ib = e * inv, -b * inv
        id_, ie = -d * inv, a * inv
        ic = -(ia * px0 + ib * py0)
        if_ = -(id_ * px0 + ie * py0)
        warped = patch.transform((W, H), Image.AFFINE, (ia, ib, ic, id_, ie, if_),
                                 resample=Image.NEAREST)
        shade = pm.SHADE[face]
        if shade < 1.0:
            rgb = warped.convert("RGB").point(lambda value, s=shade: int(value * s))
            warped = Image.merge("RGBA", (*rgb.split(), warped.getchannel("A")))
        canvas.alpha_composite(warped)

    OUT.mkdir(parents=True, exist_ok=True)
    canvas.save(out)


TARGETS = [
    ("SpiritFoxModel", "spirit_fox"),
    ("WindWolfModel", "wind_wolf"),
    ("StonehornGoatModel", "stonehorn_goat"),
    ("CrystalTurtleModel", "crystal_turtle"),
    ("EmberCraneModel", "ember_crane"),
    ("CultivationSettlerModel", "cultivation_settler_merchant"),
]


def main() -> None:
    for model_name, texture_name in TARGETS:
        model = pm.parse_model(pm.MODELS / f"{model_name}.java")
        texture = pm.ENTITY / f"{texture_name}.png"
        sheet = Image.new("RGBA", (320 * len(VIEWS), 320), (16, 18, 22, 255))
        for index, (view_name, (yaw, pitch)) in enumerate(VIEWS.items()):
            tile = OUT / f"{texture_name}_{view_name}.png"
            render_view(model, texture, (320, 320), tile, yaw, pitch)
            sheet.alpha_composite(Image.open(tile), (320 * index, 0))
        sheet.save(OUT / f"{texture_name}_sheet.png")
        print("sheet", (OUT / f"{texture_name}_sheet.png").relative_to(ROOT))


if __name__ == "__main__":
    main()
