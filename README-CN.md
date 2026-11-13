# RollingGate KCN Addon

[English](README.md) · [简体中文](README-CN.md)

一个 [RollingGate](https://github.com/Anvil-Dev/RollingGate) 的附属模组，提供额外的游戏规则与实用功能，以满足金寻在创造模式中的需求。

## 项目介绍

RollingGate KCN Addon（RGKCN）扩展了 RollingGate 的功能，主要面向创造模式建造、技术性玩法及便利性需求。

本模组通过可配置的规则和命令提供额外功能，同时默认保持原版行为。

## 功能特性

- **漏斗计数器** — 通过不同颜色的羊毛频道统计物品收集效率。
- **创造玩家无碰撞** — 允许飞行中的创造模式玩家穿过方块。
- **回响扳手** — 使用回响碎片改变方块朝向及放置方向。
- **计数器命令** — 查看和管理漏斗计数器的统计数据。

## 文档

有关可用规则、配置方式及命令的详细说明，请参阅[规则文档](docs/rules-CN.md)。

## 依赖

- [RollingGate（卷帘门）](https://github.com/Anvil-Dev/RollingGate)

## 可选联动

### [SiliconeDolls（硅胶假人）](https://github.com/Anvil-Dev/SiliconeDolls)

添加 `/player <name> rejoin` 命令支持，允许曾经生成过的假人在上次退出时所在的维度、位置和朝向重新加入世界。

对于没有历史位置记录的假人，该命令的行为与 `/player <name> spawn` 一致。

该联动为可选功能，未安装硅胶假人时，RGKCN 仍可正常使用。

## 许可证

许可证信息请参阅 [LICENSE](LICENSE)。
