# Rolling Gate KCN Addon

[English](README.md) · [简体中文](README-CN.md)

An addon for [RollingGate](https://github.com/Anvil-Dev/RollingGate), providing additional gameplay rules and utilities.

## Overview

RollingGate KCN Addon (RGKCN) extends RollingGate with additional features focused on creative mode building, technical gameplay, and convenience.

The addon introduces configurable rules and commands that provide additional functionality while preserving vanilla behavior by default.

## Features

- **Hopper Counters** — Track item collection rates across different wool channels.
- **Creative Player No-Clip** — Move through blocks while flying in Creative mode.
- **Echo Wrench** — Manipulate block orientation and placement using Echo Shards.
- **Counter Command** — View and manage hopper counter statistics.

## Documentation

For detailed information about available rules, configuration, and commands, see the [Rules Documentation](docs/rules.md).

## Requirements

- [RollingGate](https://github.com/Anvil-Dev/RollingGate)
  Optional Integrations

## Optional Integrations

### [SiliconeDolls](https://github.com/Anvil-Dev/SiliconeDolls)

Adds support for the `/player <name> rejoin` command, allowing previously spawned fake players to rejoin the world at their last saved dimension, position, and rotation.

For fake players without a saved position, the command behaves identically to `/player <name> spawn`.

This integration is optional. RGKCN works normally without SiliconeDolls installed.

## License

See [LICENSE](LICENSE) for license information.
