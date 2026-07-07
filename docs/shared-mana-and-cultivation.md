# 共享法力值与玄幻修行协议

## 核心决定

`地球 Online：玄幻` 的灵力不单独做一条孤立能量槽。灵力、魔幻分支的魔力、未来其他超自然路线的基础能量，统一显示为“法力值”。

玩家同时练玄幻与魔幻时，不互斥、不覆盖，直接加法计算：

```text
法力上限 = 基础法力
        + 玄幻路线贡献
        + 魔幻路线贡献
        + 装备贡献
        + 临时效果贡献
```

当前第一版默认：

- 基础法力：20
- 学会《引气诀》：玄幻路线贡献 +30
- 学会魔幻分支奥术启蒙：魔幻路线贡献 +30

所以两个都练后，第一版基础总上限为 80。

## 共享持久数据键

两个 mod 都读写同一组玩家持久数据键：

```text
earth_online_arcana.current_mana
earth_online_arcana.base_mana
earth_online_arcana.xuanhuan_mana_bonus
earth_online_arcana.magic_mana_bonus
earth_online_arcana.equipment_mana_bonus
earth_online_arcana.temporary_mana_bonus
earth_online_arcana.qi_absorption_rate
earth_online_arcana.magic_attunement_rate
earth_online_arcana.cultivation_level
earth_online_arcana.magic_research_level
```

玄幻分支只负责写入：

- `xuanhuan_mana_bonus`
- `qi_absorption_rate`
- `cultivation_level`
- 当前法力的恢复值

它不会清空或覆盖魔幻分支的数据。

## 功法和灵气

第一版功法入口是 `引气诀`：

- 可由一块泥土、任意木板或任意石头合成，保证新手能立刻进入路线。
- 首次右键学习，增加玄幻路线法力上限和灵气吸收率。
- 之后右键表示运转功法，按当前位置灵气浓度恢复少量法力。

灵气由 `Spirituality.measure(...)` 临时估算：

- 主世界基础灵气高于其他维度。
- 地下、山地、高处和局部噪声会影响灵气。
- 后续接入真正灵脉、灵泉、地质结构和生态联动后，这个函数应扩展而不是推翻。

## 与魔幻分支联动

魔幻分支的魔力调律与玄幻分支的灵气吸收等价为同一条“法力值”。

设计上要保持：

- 玄幻叫“灵力路线贡献”，魔幻叫“魔力路线贡献”。
- UI 和手册对玩家说“二者都是法力值来源”。
- 如果另一个 mod 不存在，本 mod 仍可独立运行。
- 暂不抽第三个 API mod；等数据键和玩法稳定后，再考虑 `earth_online_arcana_api`。

## 后续开发顺序

1. 把 `引气诀` 扩展为功法树：入门功法、吐纳法、炼气法、阵法辅助功法。
2. 给聚灵阵核心接入区域恢复加成。
3. 加丹药和符箓对当前法力、恢复速度、临时上限的影响。
4. 手册 GUI 展示当前法力、灵力贡献、魔力贡献和下一步。
5. JEI/手册把功法、炼丹、符箓、阵法分成不同页面，不和魔幻炼金混在一起。
