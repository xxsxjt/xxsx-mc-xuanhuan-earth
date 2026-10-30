import io, json, os

base = r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2\src\main\resources\data\xuanhuan_earth\worldgen'
conf = os.path.join(base, 'configured_feature')
placed = os.path.join(base, 'placed_feature')

SPECS = {
    'mountain_spirit_shrine': 950,
    'spirit_beast_keeper_camp': 780,
}

for name, chance in SPECS.items():
    configured = {"feature": f"xuanhuan_earth:{name}", "placement": []}
    # Configured features for NoneFeatureConfiguration hold an empty config object.
    io.open(os.path.join(conf, name + '.json'), 'w', encoding='utf-8', newline='\n').write(
        json.dumps({"config": {}}, indent=2) + '\n')

    placement = {
        "feature": f"xuanhuan_earth:{name}",
        "placement": [
            {"type": "minecraft:rarity_filter", "chance": chance},
            {"type": "minecraft:in_square"},
            {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING"},
            {"type": "minecraft:biome"},
        ],
    }
    io.open(os.path.join(placed, name + '.json'), 'w', encoding='utf-8', newline='\n').write(
        json.dumps(placement, indent=2) + '\n')
    print(name, chance)
