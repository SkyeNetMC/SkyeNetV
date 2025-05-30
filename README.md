# SkyeNetV

A Velocity plugin for SkyeNet Minecraft server network.

## Features

- Discord integration
- Server management commands
- Server rules system

## Commands

### /rules

View and manage server rules:
- `/rules` - Display all server rules
- `/rules add "<title>" "<description>"` - Add a new rule (Admin only)
- `/rules remove <id>` - Remove a rule (Admin only)
- `/rules edit <id> "<title>" "<description>"` - Edit an existing rule (Admin only)
- `/rules header <text>` - Set the rules header text (Admin only)
- `/rules footer <text>` - Set the rules footer text (Admin only)
- `/rules reload` - Reload rules from config file (Admin only)

### Permissions

- `skyenetv.rules.admin` - Access to all rule management commands

## Configuration

Server rules are stored in `plugins/skyenetv/rules.json`.

## MiniMessage Format

The plugin uses MiniMessage format for colorful text. Examples:
- `<red>No Hacking</red>`
- `<gradient:gold:yellow>SkyeNet Rules</gradient>`
- `<gray>Thank you for following our rules!</gray>`
