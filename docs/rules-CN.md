# 规则

[English](rules.md) · [简体中文](rules-CN.md)

## commandCounter

启用 `/counter` 命令，用于显示和管理漏斗计数器的统计数据。

**默认值**：`ops`

### /rg commandCounter [ops|true|false|1|2|3|4]

控制 `/counter` 命令的可用性及权限等级。

### /counter

显示各频道漏斗计数器记录的物品收集统计数据。

#### 用法

`/counter` 命令有两个子命令（又称指令）：`realtime` 和 `reset`。

这两个子命令既可以直接跟在 `/counter` 后面，用于操作所有频道，也可以跟在 `<channel>` 参数后面，用于操作指定频道。

- `realtime` — 使用现实时间而非游戏时间显示计数器统计数据。
- `reset` — 重置计数器。

默认情况下，`/counter` 会显示所有频道的计数器数据。也可以指定 `<channel>` 参数，以显示或重置特定频道的计数器。

#### 语法

- `/counter [realtime|reset]`
- `/counter <channel> [realtime|reset]`

#### 参数

- `<channel>`：指定要显示或重置其计数器的频道。必须为 16 种染料颜色之一。

## hopperCounters

使朝向任意羊毛方块的漏斗能够作为计数器使用。不同颜色的羊毛分别代表不同的频道。

**默认值**：`false`

### /rg hopperCounters [true|false]

启用或禁用漏斗计数器功能。

## creativePlayerNoClip

允许飞行中的创造模式玩家穿过方块，且不会触发方块更新。

**默认值**：`false`

### /rg creativePlayerNoClip [true|false]

启用或禁用创造模式飞行玩家的无碰撞功能。

## echoWrench

允许使用回响碎片作为扳手来改变方块朝向：

- **主手**：手持回响碎片右键方块，可以改变方块朝向。
- **副手**：副手持有回响碎片时，主手放置方块将反转方块的默认放置朝向。
- **副手**：副手持有回响碎片且主手为空手时，右键方块可以改变方块朝向。

改变方块朝向时，旋转方向取决于玩家的视角：玩家平视目标方块时，方块按照顺时针方向旋转；玩家仰视或俯视目标方块时，则反转其上下朝向。

**默认值**：`false`

### /rg echoWrench [true|false]

启用或禁用回响扳手功能。
