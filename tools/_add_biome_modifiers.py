import io, json, os

base = (r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2'
        r'\src\main\resources\data\xuanhuan_earth\neoforge\biome_modifier')

SPECS = {
    'add_mountain_spirit_shrines.json': {
        "type": "neoforge:add_features",
        "biomes": "#minecraft:is_mountain",
        "features": ["xuanhuan_earth:mountain_spirit_shrine"],
        "step": "surface_structures",
    },
    'add_spirit_beast_keeper_camps.json': {
        "type": "neoforge:add_features",
        "biomes": "#minecraft:is_forest",
        "features": ["xuanhuan_earth:spirit_beast_keeper_camp"],
        "step": "surface_structures",
    },
}

for name, payload in SPECS.items():
    io.open(os.path.join(base, name), 'w', encoding='utf-8', newline='\n').write(
        json.dumps(payload, indent=2) + '\n')
    print('wrote', name)
