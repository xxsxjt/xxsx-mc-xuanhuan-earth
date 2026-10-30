import io, re

p = (r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2\src\main\java'
     r'\com\xxsx\xuanhuanearth\client\XuanhuanHandbookScreen.java')
s = io.open(p, encoding='utf-8').read()
for m in re.finditer(r'page\("([^"]*)"', s):
    line = s[:m.start()].count('\n') + 1
    print(line, m.group(1))
