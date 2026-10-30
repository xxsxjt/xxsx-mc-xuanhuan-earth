import io

p = (r'D:\Codex\WORKSPACES\Projects\mc-agent\earth-series\xuanhuan_earth\neoforge-26.2\src\main\java'
     r'\com\xxsx\xuanhuanearth\client\CultivationScreen.java')
s = io.open(p, encoding='utf-8').read()

# 1. Field for the breakthrough button.
old_field = '''    private Button primaryActionButton;'''
new_field = '''    private Button primaryActionButton;
    private Button breakthroughButton;'''
if old_field in s:
    s = s.replace(old_field, new_field, 1)
    print('field added')
else:
    print('MISS field')

# 2. Create the button next to the technique button.
old_btn = '''        primaryActionButton = addRenderableWidget(Button.builder(
                        Component.translatable("screen.xuanhuan_earth.cultivation.practice"),
                        ignored -> performPrimaryAction())
                .bounds(left + 10, top + panelHeight() - 28, buttonWidth, 20)
                .build());'''
new_btn = '''        breakthroughButton = addRenderableWidget(Button.builder(
                        Component.translatable("screen.xuanhuan_earth.cultivation.breakthrough"),
                        ignored -> XuanhuanEarthClient.requestBreakthrough())
                .bounds(left + 10, top + panelHeight() - 72, buttonWidth, 18)
                .build());
        primaryActionButton = addRenderableWidget(Button.builder(
                        Component.translatable("screen.xuanhuan_earth.cultivation.practice"),
                        ignored -> performPrimaryAction())
                .bounds(left + 10, top + panelHeight() - 28, buttonWidth, 20)
                .build());'''
if old_btn in s:
    s = s.replace(old_btn, new_btn, 1)
    print('button added')
else:
    print('MISS button')

io.open(p, 'w', encoding='utf-8', newline='\n').write(s)
