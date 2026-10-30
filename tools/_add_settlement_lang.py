import io, json, collections, os

lang = r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2\src\main\resources\assets\xuanhuan_earth\lang'

ADD = {
    'zh_cn': {
        'settlement.xuanhuan_earth.mountain_spirit_shrine': '\u5c71\u795e\u7960',
        'settlement.xuanhuan_earth.spirit_beast_keeper_camp': '\u7075\u517d\u5e08\u8425\u5730',
    },
    'en_us': {
        'settlement.xuanhuan_earth.mountain_spirit_shrine': 'Mountain Spirit Shrine',
        'settlement.xuanhuan_earth.spirit_beast_keeper_camp': 'Spirit Beast Keeper Camp',
    },
}

for loc, entries in ADD.items():
    path = os.path.join(lang, loc + '.json')
    data = json.loads(io.open(path, encoding='utf-8').read(),
                      object_pairs_hook=collections.OrderedDict)
    added = 0
    for key, value in entries.items():
        if key not in data:
            data[key] = value
            added += 1
    io.open(path, 'w', encoding='utf-8', newline='\n').write(
        json.dumps(data, ensure_ascii=False, indent=2) + '\n')
    print(loc, 'added', added)
