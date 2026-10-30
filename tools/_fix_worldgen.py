import io, json, os

base = r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2\src\main\resources\data\xuanhuan_earth\worldgen'
conf = os.path.join(base, 'configured_feature')

for name in ('mountain_spirit_shrine', 'spirit_beast_keeper_camp'):
    payload = {"type": f"xuanhuan_earth:{name}", "config": {}}
    io.open(os.path.join(conf, name + '.json'), 'w', encoding='utf-8', newline='\n').write(
        json.dumps(payload, indent=2) + '\n')
    print(name, 'fixed')
