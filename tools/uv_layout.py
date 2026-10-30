"""Lay out box UVs for a model spec and paint its texture.

Hand-packing UV offsets is how box models end up with smeared faces. This allocator takes
a part/box spec, reserves a rectangle for every box using vanilla's unwrap layout, emits
the Java ``texOffs`` values, and paints each face from a palette so the result is legible
in the offline preview.
"""

from __future__ import annotations

import json
import random
from dataclasses import dataclass, field
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ENTITY = ROOT / "neoforge-26.2" / "src" / "main" / "resources" / "assets" / "xuanhuan_earth" / "textures" / "entity"
OUT = ROOT / "tmp" / "uv-layout"


@dataclass
class BoxSpec:
    name: str
    x: float
    y: float
    z: float
    w: float
    h: float
    d: float
    style: str = "fur"
    grow: float = 0.0
    uv: tuple[int, int] = (0, 0)


@dataclass
class PartSpec:
    name: str
    pivot: tuple[float, float, float]
    rot: tuple[float, float, float] = (0.0, 0.0, 0.0)
    boxes: list[BoxSpec] = field(default_factory=list)
    children: list["PartSpec"] = field(default_factory=list)
    parent: str | None = None


def box_extent(w: int, h: int, d: int) -> tuple[int, int]:
    return (max(w, d) * 2, d + h)


class Allocator:
    def __init__(self, size: int) -> None:
        self.size = size
        self.cursor_x = 0
        self.cursor_y = 0
        self.row_height = 0
        self.assigned: dict[str, tuple[int, int]] = {}

    def allocate(self, key: str, w: int, h: int, d: int) -> tuple[int, int]:
        bw, bh = box_extent(w, h, d)
        bw += 1
        bh += 1
        if self.cursor_x + bw > self.size:
            self.cursor_x = 0
            self.cursor_y += self.row_height
            self.row_height = 0
        if self.cursor_y + bh > self.size:
            raise RuntimeError(f"texture {self.size} too small at {key}")
        uv = (self.cursor_x, self.cursor_y)
        self.assigned[key] = uv
        self.cursor_x += bw
        self.row_height = max(self.row_height, bh)
        return uv


def face_rects(u: int, v: int, w: int, h: int, d: int) -> dict[str, tuple[int, int, int, int]]:
    return {
        "down": (u + d, v, u + d + w, v + d),
        "up": (u + d + w, v, u + d + w + w, v + d),
        "west": (u, v + d, u + d, v + d + h),
        "north": (u + d, v + d, u + d + w, v + d + h),
        "east": (u + d + w, v + d, u + d + w + d, v + d + h),
        "south": (u + d + w + d, v + d, u + d + w + d + w, v + d + h),
    }


def shade(color: tuple[int, int, int], delta: int) -> tuple[int, int, int, int]:
    return tuple(max(0, min(255, c + delta)) for c in color) + (255,)


FACE_LIGHT = {"up": 20, "down": -30, "west": -8, "north": 6, "east": 10, "south": -14}


def paint_box(image: Image.Image, u: int, v: int, box: BoxSpec,
              base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    draw = ImageDraw.Draw(image)
    w, h, d = int(box.w), int(box.h), int(box.d)
    rng = random.Random(sum(ord(c) for c in box.name))
    for face, rect in face_rects(u, v, w, h, d).items():
        x0, y0, x1, y1 = rect
        if x1 <= x0 or y1 <= y0:
            continue
        tone = shade(base, FACE_LIGHT[face])
        draw.rectangle((x0, y0, x1 - 1, y1 - 1), fill=tone)
        if x1 - x0 > 2 and y1 - y0 > 2:
            draw.line((x0, y0, x1 - 1, y0), fill=shade(base, FACE_LIGHT[face] + 16))
            draw.line((x0, y1 - 1, x1 - 1, y1 - 1), fill=shade(base, FACE_LIGHT[face] - 26))
        if box.style == "fur":
            # Two-tone fur: darker guard hairs along the back, pale underbelly.
            dark = shade(base, FACE_LIGHT[face] - 26)
            for y in range(y0 + 1, y1 - 1, 2):
                for x in range(x0 + 1 + (y & 1), x1 - 1, 3):
                    delta = rng.choice((-22, -14, 12, 20))
                    draw.point((x, y), fill=shade(base, FACE_LIGHT[face] + delta))
            if y1 - y0 >= 6:
                mid = y0 + (y1 - y0) // 3
                for x in range(x0 + 1, x1 - 1):
                    if (x + y0) % 3 != 0:
                        draw.point((x, mid), fill=dark)
        elif box.style == "crystal":
            for offset in range(-(y1 - y0), x1 - x0, 3):
                draw.line((x0 + offset, y1 - 1, x0 + offset + (y1 - y0), y0),
                          fill=shade(accent, 34))
            for offset in range(-(y1 - y0), x1 - x0, 6):
                draw.line((x0 + offset + 1, y1 - 1, x0 + offset + 1 + (y1 - y0), y0),
                          fill=shade(accent, -30))
        elif box.style == "metal":
            if y1 - y0 >= 4:
                mid = y0 + (y1 - y0) // 2
                draw.line((x0, mid, x1 - 1, mid), fill=shade(base, FACE_LIGHT[face] - 26))
                draw.line((x0, mid - 1, x1 - 1, mid - 1), fill=shade(base, FACE_LIGHT[face] + 18))
        elif box.style == "feather":
            for y in range(y0 + 1, y1 - 1, 2):
                draw.line((x0 + 1, y, x1 - 2, y - 1), fill=shade(accent, 4))
            for y in range(y0 + 2, y1 - 1, 4):
                draw.line((x0 + 2, y, x1 - 3, y - 1), fill=shade(base, FACE_LIGHT[face] - 18))


def emit_java_offsets(specs: list[PartSpec]) -> str:
    lines = []

    def walk(part: PartSpec, depth: int):
        pad = "    " * depth
        for box in part.boxes:
            lines.append(f'{pad}{part.name}.{box.name} uv=({box.uv[0]}, {box.uv[1]}) '
                         f'dims=({box.w:g}, {box.h:g}, {box.d:g})')
        for child in part.children:
            walk(child, depth + 1)

    for part in specs:
        walk(part, 0)
    return "\n".join(lines)


def build(name: str, size: int, specs: list[PartSpec],
          base: tuple[int, int, int], accent: tuple[int, int, int]) -> dict:
    allocator = Allocator(size)
    image = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    result = {}

    def walk(part: PartSpec):
        for box in part.boxes:
            uv = allocator.allocate(f"{part.name}.{box.name}",
                                    int(box.w), int(box.h), int(box.d))
            box.uv = uv
            paint_box(image, uv[0], uv[1], box, base, accent)
        for child in part.children:
            walk(child)

    for spec in specs:
        walk(spec)

    OUT.mkdir(parents=True, exist_ok=True)
    image.save(OUT / f"{name}.png")
    ENTITY.mkdir(parents=True, exist_ok=True)
    image.save(ENTITY / f"{name}.png")
    print("===", name, size)
    print(emit_java_offsets(specs))
    return {"size": size, "texture": name}
