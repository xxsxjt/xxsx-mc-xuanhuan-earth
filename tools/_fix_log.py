import io

p = (r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2\src\main\java'
     r'\com\xxsx\xuanhuanearth\settlement\XuanhuanSettlementAnchorBlockEntity.java')
s = io.open(p, encoding='utf-8').read()

old = ('XuanhuanEarth.LOGGER.info("Initialized xuanhuan settlement {} ({}) at {}",\n'
       '                settlementType, palette, pos);')
new = ('XuanhuanEarth.LOGGER.info("Initialized xuanhuan settlement {} layout={} palette={} '
       'residents={} at {}",\n'
       '                settlementType, shape, palette, roster.size(), pos);')

if old in s:
    s = s.replace(old, new, 1)
    io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
    print('updated')
else:
    print('MISS')
