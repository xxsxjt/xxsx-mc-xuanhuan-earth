import io, json, collections

LANG = r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2\src\main\resources\assets\xuanhuan_earth\lang'

ZH = {
    'realm.xuanhuan_earth.0': '凡身',
    'realm.xuanhuan_earth.1': '炼气',
    'realm.xuanhuan_earth.2': '筑基',
    'realm.xuanhuan_earth.3': '金丹',
    'realm.xuanhuan_earth.4': '元婴',
    'realm.xuanhuan_earth.5': '化神',
    'screen.xuanhuan_earth.cultivation.breakthrough': '破境',
    'message.xuanhuan_earth.realm.max': '已至化神，此界再无可破之境。',
    'message.xuanhuan_earth.realm.route_low': '%s 尚未达到 %s 级（当前 %s 级），根基不足，无法破境。',
    'message.xuanhuan_earth.realm.needs_pill': '破境需要一枚%s。',
    'message.xuanhuan_earth.realm.advanced': '破境成功：踏入%s（第 %s 境），法力上限提升至 %s。',
}

EN = {
    'realm.xuanhuan_earth.0': 'Mortal',
    'realm.xuanhuan_earth.1': 'Qi Refining',
    'realm.xuanhuan_earth.2': 'Foundation',
    'realm.xuanhuan_earth.3': 'Golden Core',
    'realm.xuanhuan_earth.4': 'Nascent Soul',
    'realm.xuanhuan_earth.5': 'Spirit Transformation',
    'screen.xuanhuan_earth.cultivation.breakthrough': 'Break Through',
    'message.xuanhuan_earth.realm.max': 'You have reached Spirit Transformation; nothing higher exists in this world.',
    'message.xuanhuan_earth.realm.route_low': '%s has not reached level %s (currently %s); your foundation is too shallow to break through.',
    'message.xuanhuan_earth.realm.needs_pill': 'A breakthrough requires one %s.',
    'message.xuanhuan_earth.realm.advanced': 'Breakthrough: you enter %s (realm %s) and your mana cap rises to %s.',
}

for loc, entries in (('zh_cn', ZH), ('en_us', EN)):
    path = f'{LANG}\\{loc}.json'
    data = json.loads(io.open(path, encoding='utf-8').read(),
                      object_pairs_hook=collections.OrderedDict)
    added = 0
    for k, v in entries.items():
        if k not in data:
            data[k] = v
            added += 1
    io.open(path, 'w', encoding='utf-8', newline='\n').write(
        json.dumps(data, ensure_ascii=False, indent=2) + '\n')
    print(loc, 'added', added, 'total', len(data))
