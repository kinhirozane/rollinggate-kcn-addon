# Rules

[English](rules.md) · [简体中文](rules-CN.md)

## commandCounter

Enables the `/counter` command, which displays and manages hopper counter statistics.

**Default value**: `ops`

### /rg commandCounter [ops|true|false|1|2|3|4]

Controls the availability and permission level of the `/counter` command.

### /counter

Displays the item collection statistics recorded by hopper counters, grouped by channel.

#### Usage

The `/counter` command has two subcommands (aka. instructions): `realtime` and `reset`.
Both subcommands can be used either directly after `/counter` to operate on all channels or after
a `<channel>` argument to operate on the specified channel.

- `realtime` — Displays counter statistics using real time instead of game time.
- `reset` — Resets the counters.

By default, `/counter` displays the counters of all channels. A `<channel>` argument can be
specified to display or reset the counter of a specific channel.

#### Syntax

- `/counter [realtime|reset]`
- `/counter <channel> [realtime|reset]`

#### Arguments

- `<channel>`: Specifies the channel whose counter is to be displayed or reset.
  Must be one of the 16 dye colors.

## hopperCounters

Enables hoppers facing any wool block to function as counters. Each wool color represents a separate channel.

**Default value**: `false`

### /rg hopperCounters [true|false]

Enables or disables hopper counters.

## creativePlayerNoClip

Allows flying players in Creative mode to pass through blocks without triggering block updates.

**Default value:** `false`

### /rg creativePlayerNoClip [true|false]

Enables or disables no-clip for flying Creative mode players.

## echoWrench

Allows Echo Shards to be used as wrenches to manipulate block orientation:

- **Main hand:** Right-clicking a block while holding an Echo Shard rotates the block.
- **Offhand:** Placing a block while holding an Echo Shard in the offhand reverses the block's default placement orientation.
- **Offhand:** Right-clicking a block with an empty main hand while holding an Echo Shard in the offhand rotates the block.

When rotating a block, the direction of rotation depends on the player's viewing angle: looking horizontally at the target block rotates it clockwise, while looking upward or downward reverses its vertical orientation.

**Default value:** `false`

### /rg echoWrench [true|false]

Enables or disables the Echo Wrench.
