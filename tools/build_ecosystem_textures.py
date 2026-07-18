from __future__ import annotations

import random
from pathlib import Path

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parents[1]
MASTER = ROOT / "tmp" / "imagegen" / "ecosystem-0.7"
XUANHUAN = ROOT / "neoforge-26.2" / "src" / "main" / "resources" / "assets" / "earth_online_xuanhuan"


def palette_from(path: Path, crop: tuple[int, int, int, int], fallback: list[str]) -> list[tuple[int, int, int]]:
    if not path.exists():
        return [hex_color(value) for value in fallback]
    image = Image.open(path).convert("RGB").crop(crop)
    quantized = image.quantize(colors=12, method=Image.Quantize.MEDIANCUT)
    colors = quantized.getpalette()[:36]
    counts = quantized.getcolors() or []
    ranked = sorted(counts, reverse=True)
    result: list[tuple[int, int, int]] = []
    for _, index in ranked:
        color = tuple(colors[index * 3:index * 3 + 3])
        brightness = sum(color) / 3
        if brightness < 24 or brightness > 242:
            continue
        if all(sum(abs(a - b) for a, b in zip(color, old)) > 42 for old in result):
            result.append(color)
        if len(result) == 7:
            break
    return result if len(result) >= 4 else [hex_color(value) for value in fallback]


def hex_color(value: str) -> tuple[int, int, int]:
    value = value.removeprefix("#")
    return tuple(int(value[index:index + 2], 16) for index in (0, 2, 4))


def shade(color: tuple[int, int, int], delta: int) -> tuple[int, int, int, int]:
    return tuple(max(0, min(255, channel + delta)) for channel in color) + (255,)


def material_texture(size: tuple[int, int], palette: list[tuple[int, int, int]], seed: int,
                     accent: tuple[int, int, int] | None = None, plate: bool = False) -> Image.Image:
    random.seed(seed)
    width, height = size
    image = Image.new("RGBA", size, shade(palette[0], -8))
    draw = ImageDraw.Draw(image)
    step = 2
    for y in range(0, height, step):
        for x in range(0, width, step):
            base = palette[(x // step * 3 + y // step * 5 + random.randrange(len(palette))) % len(palette)]
            variation = random.choice((-18, -10, -4, 0, 5, 11))
            draw.rectangle((x, y, min(width - 1, x + step - 1), min(height - 1, y + step - 1)), fill=shade(base, variation))
    if plate:
        for y in range(5, height, 12):
            draw.line((0, y, width - 1, y), fill=shade(palette[0], -38), width=1)
        for x in range(7, width, 18):
            draw.line((x, 0, x, height - 1), fill=shade(palette[-1], -25), width=1)
    if accent:
        for offset in range(-height, width, 18):
            points = []
            for y in range(height):
                x = offset + y + int(2 * ((y // 4) % 2))
                if 0 <= x < width:
                    points.append((x, y))
            if len(points) > 3:
                draw.line(points, fill=shade(accent, 12), width=1)
                if offset % 36 == 0:
                    draw.point(points[len(points) // 2], fill=(235, 255, 255, 255))
    return image


def save_entity(name: str, size: tuple[int, int], master: str, crop: tuple[int, int, int, int],
                fallback: list[str], seed: int, accent: str | None = None, plate: bool = False) -> None:
    palette = palette_from(MASTER / master, crop, fallback)
    image = material_texture(size, palette, seed, hex_color(accent) if accent else None, plate)
    out = XUANHUAN / "textures" / "entity" / f"{name}.png"
    out.parent.mkdir(parents=True, exist_ok=True)
    image.save(out)


def egg_icon(path: Path, base: str, spots: str, rune: str) -> None:
    image = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    b = hex_color(base)
    s = hex_color(spots)
    r = hex_color(rune)
    for y in range(3, 29):
        half = int(10 * (1.0 - abs(y - 17) / 18.0)) + 2
        for x in range(16 - half, 17 + half):
            edge = min(x - (16 - half), (16 + half) - x, y - 3, 28 - y)
            draw.point((x, y), fill=shade(b, -18 if edge < 1 else (10 if (x + y) % 5 == 0 else 0)))
    for x, y in ((11, 10), (20, 13), (13, 20), (21, 23), (16, 7)):
        draw.rectangle((x, y, x + 2, y + 2), fill=shade(s, 4))
    draw.line((12, 17, 16, 13, 20, 17, 16, 22, 12, 17), fill=shade(r, 18), width=1)
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def seal_icon(path: Path) -> None:
    image = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    jade = hex_color("3b9b8d")
    gold = hex_color("d8b45a")
    paper = hex_color("e7dfc4")
    draw.rounded_rectangle((5, 3, 26, 28), radius=2, fill=shade(paper, 0), outline=shade(gold, -35), width=2)
    draw.rectangle((8, 6, 23, 25), outline=shade(jade, -12), width=1)
    draw.ellipse((10, 9, 21, 20), outline=shade(jade, 15), width=2)
    draw.line((12, 15, 16, 11, 20, 15, 16, 21, 12, 15), fill=shade(gold, 8), width=1)
    draw.rectangle((13, 23, 18, 29), fill=shade(jade, -8))
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def main() -> None:
    save_entity("wind_wolf", (96, 96), "wind_wolf.png", (360, 80, 1160, 760),
                ["344252", "607286", "a9b8c7", "d9e5ec", "36b8d1"], 701, "42d7ef")
    save_entity("stonehorn_goat", (128, 96), "stonehorn_goat.png", (250, 40, 1250, 690),
                ["302d2b", "55504a", "777067", "a59684", "d88925"], 702, "e99a2f", True)
    save_entity("crystal_turtle", (96, 80), "crystal_turtle.png", (100, 80, 1450, 760),
                ["173f35", "2b6656", "777a70", "b4a66d", "36d7d8"], 703, "59ebeb", True)
    save_entity("ember_crane", (112, 80), "ember_crane.png", (100, 40, 1450, 760),
                ["2c2a28", "d7c69f", "f0e3c1", "bb3d25", "eea83a"], 704, "f2a83d")

    resident_master = MASTER / "cultivation_settler.png"
    resident_specs = [
        ("cultivation_settler_merchant", (120, 60, 740, 430), ["34332f", "75634d", "9b8c70", "426c5c", "d1c8ac"], 711, "55a796"),
        ("cultivation_settler_spring_keeper", (110, 400, 800, 760), ["263b38", "4c7c75", "9bb5a9", "dedbc8", "43b8a5"], 712, "58d8c4"),
        ("cultivation_settler_steward", (120, 700, 800, 1020), ["171b21", "303743", "8b7650", "c4b68f", "285d66"], 713, "c7a45c"),
    ]
    for name, crop, fallback, seed, accent in resident_specs:
        palette = palette_from(resident_master, crop, fallback)
        image = material_texture((64, 64), palette, seed, hex_color(accent), True)
        out = XUANHUAN / "textures" / "entity" / f"{name}.png"
        out.parent.mkdir(parents=True, exist_ok=True)
        image.save(out)

    item_dir = XUANHUAN / "textures" / "item"
    seal_icon(item_dir / "spirit_beast_seal.png")
    egg_icon(item_dir / "wind_wolf_spawn_egg.png", "566b7f", "dbe8ed", "42d7ef")
    egg_icon(item_dir / "stonehorn_goat_spawn_egg.png", "514b45", "81786e", "e99a2f")
    egg_icon(item_dir / "crystal_turtle_spawn_egg.png", "24594a", "777a70", "59ebeb")
    egg_icon(item_dir / "ember_crane_spawn_egg.png", "e5d8b7", "b93f28", "f2a83d")
    egg_icon(item_dir / "cultivation_settler_spawn_egg.png", "34332f", "4c7c75", "d4b25e")


if __name__ == "__main__":
    main()
