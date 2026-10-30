"""Third pass: contrast, not just geometry.

The four-view sheets showed the silhouettes are now roughly right but every creature reads
as a pale blob, because the fur painter only jitters +/-20 around one base colour. This pass
gives each box a real two-tone scheme (dark dorsal, pale ventral) and adds a darker outline
on the silhouette edges, which is what makes a Minecraft mob legible.
"""

from __future__ import annotations

import io
import random
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
UV = ROOT / 'tools' / 'uv_layout.py'

NEW_PAINT = '''def paint_box(image: Image.Image, u: int, v: int, box: BoxSpec,
              base: tuple[int, int, int], accent: tuple[int, int, int]) -> None:
    draw = ImageDraw.Draw(image)
    w, h, d = int(box.w), int(box.h), int(box.d)
    rng = random.Random(sum(ord(c) for c in box.name))
    # Two-tone: the dorsal half is darker than the base, the ventral half lighter. Without
    # this a single-colour fur texture makes every creature read as a white blob.
    dorsal = tuple(max(0, c - 46) for c in base)
    ventral = tuple(min(255, c + 34) for c in base)
    for face, rect in face_rects(u, v, w, h, d).items():
        x0, y0, x1, y1 = rect
        if x1 <= x0 or y1 <= y0:
            continue
        tone = shade(base, FACE_LIGHT[face])
        draw.rectangle((x0, y0, x1 - 1, y1 - 1), fill=tone)

        if box.style == "fur" and y1 - y0 >= 4:
            split = y0 + max(1, (y1 - y0) // 2)
            draw.rectangle((x0, y0, x1 - 1, split - 1), fill=shade(dorsal, FACE_LIGHT[face] // 2))
            draw.rectangle((x0, split, x1 - 1, y1 - 1), fill=shade(ventral, FACE_LIGHT[face] // 3))
            for y in range(y0 + 1, split, 2):
                for x in range(x0 + 1 + (y & 1), x1 - 1, 3):
                    draw.point((x, y), fill=shade(dorsal, FACE_LIGHT[face] // 2 + rng.choice((-16, 10))))
            for y in range(split + 1, y1 - 1, 2):
                for x in range(x0 + 1 + (y & 1), x1 - 1, 3):
                    draw.point((x, y), fill=shade(ventral, FACE_LIGHT[face] // 3 + rng.choice((-14, 12))))
        elif box.style == "fur":
            for y in range(y0 + 1, y1 - 1, 2):
                for x in range(x0 + 1 + (y & 1), x1 - 1, 3):
                    draw.point((x, y), fill=shade(base, FACE_LIGHT[face] + rng.choice((-18, 14))))

        if box.style == "crystal":
            for offset in range(-(y1 - y0), x1 - x0, 3):
                draw.line((x0 + offset, y1 - 1, x0 + offset + (y1 - y0), y0),
                          fill=shade(accent, 40))
            for offset in range(-(y1 - y0), x1 - x0, 6):
                draw.line((x0 + offset + 1, y1 - 1, x0 + offset + 1 + (y1 - y0), y0),
                          fill=shade(accent, -40))
        elif box.style == "metal":
            if y1 - y0 >= 4:
                mid = y0 + (y1 - y0) // 2
                draw.line((x0, mid, x1 - 1, mid), fill=shade(base, FACE_LIGHT[face] - 34))
                draw.line((x0, mid - 1, x1 - 1, mid - 1), fill=shade(base, FACE_LIGHT[face] + 26))
        elif box.style == "feather":
            for y in range(y0 + 1, y1 - 1, 2):
                draw.line((x0 + 1, y, x1 - 2, y - 1), fill=shade(accent, 8))
            for y in range(y0 + 2, y1 - 1, 4):
                draw.line((x0 + 2, y, x1 - 3, y - 1), fill=shade(base, FACE_LIGHT[face] - 26))

        # Dark border on every face: reads as an outline in-game and separates parts.
        if x1 - x0 > 2 and y1 - y0 > 2:
            edge = shade(base, FACE_LIGHT[face] - 52)
            draw.line((x0, y0, x1 - 1, y0), fill=shade(base, FACE_LIGHT[face] + 30))
            draw.line((x0, y0, x0, y1 - 1), fill=shade(base, FACE_LIGHT[face] + 16))
            draw.line((x0, y1 - 1, x1 - 1, y1 - 1), fill=edge)
            draw.line((x1 - 1, y0, x1 - 1, y1 - 1), fill=shade(base, FACE_LIGHT[face] - 34))
'''


def main() -> None:
    s = io.open(UV, encoding='utf-8').read()
    start = s.index('def paint_box(')
    end = s.index('def emit_java_offsets(')
    s = s[:start] + NEW_PAINT + '\n\n' + s[end:]
    io.open(UV, 'w', encoding='utf-8', newline='\n').write(s)
    print('paint_box replaced')
