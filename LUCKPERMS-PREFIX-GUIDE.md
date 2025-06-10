# LuckPerms Prefix Integration Guide

## Overview

The SkyeNetV plugin now supports LuckPerms prefix integration through the `{luckperms_prefix}` placeholder. This allows you to display player ranks/prefixes in all Discord messages and network announcements.

## How It Works

The plugin automatically detects and extracts player prefixes from LuckPerms and makes them available as a placeholder in message formatting. The prefix is converted to plain text for Discord compatibility while preserving the original MiniMessage formatting for in-game messages.

## Supported Placeholders

All message formats now support these placeholders:

- `{player}` - Player username
- `{luckperms_prefix}` - Player's LuckPerms prefix (if available)
- `{server}` - Server name (where applicable)
- `{from}` / `{to}` - Server names for switch messages
- `{name}` / `{message}` - For Discord messages

## Configuration Examples

### Chat Messages
```yaml
messages:
  chat_prefix: "<gray>[<blue>{server}</blue>]</gray> {luckperms_prefix}<white><bold>{player}</bold>:</white> "
```

**Result**: `[Survival] [Admin] PlayerName: Hello everyone!`

### Join/Leave Messages
```yaml
messages:
  join: "<green>✅ {luckperms_prefix}<bold>{player}</bold> joined the network!</green>"
  leave: "<red>❌ {luckperms_prefix}<bold>{player}</bold> left the network!</red>"
```

**Result**: 
- `✅ [VIP] PlayerName joined the network!`
- `❌ [VIP] PlayerName left the network!`

### Server Switch Messages
```yaml
messages:
  server_switch: "<yellow>🔄 {luckperms_prefix}<bold>{player}</bold> switched from <italic>{from}</italic> to <italic>{to}</italic></yellow>"
```

**Result**: `🔄 [Moderator] PlayerName switched from Lobby to Survival`

### Network Messages
```yaml
network:
  join_format: "<green>✅ {luckperms_prefix}<bold>{player}</bold> joined the network!</green>"
  leave_format: "<red>❌ {luckperms_prefix}<bold>{player}</bold> left the network!</red>"
```

## Discord Integration

When messages are sent to Discord, the LuckPerms prefix is automatically converted to plain text:

**In-game**: `[§4Admin§r] PlayerName joined!` (with colors)
**Discord**: `[Admin] PlayerName joined!` (plain text)

## Fallback Behavior

- If LuckPerms is not installed: `{luckperms_prefix}` becomes an empty string
- If player has no prefix: `{luckperms_prefix}` becomes an empty string
- If LuckPerms fails to load: Plugin logs a warning and continues without prefix support

## LuckPerms Prefix Examples

Your LuckPerms prefixes can use MiniMessage formatting:

```yaml
# In LuckPerms
prefix: "<red>[Admin]</red> "
prefix: "<gold>[VIP]</gold> "
prefix: "<green>[Member]</green> "
prefix: "<blue>[Moderator]</blue> "
```

## Testing the Feature

1. **Ensure LuckPerms is installed** on your network
2. **Set up player groups** with prefixes in LuckPerms
3. **Update your config.yml** with the new placeholder examples
4. **Restart your server**
5. **Test the messages**:
   - Join/leave the server
   - Switch between servers
   - Send chat messages
   - Check Discord for proper formatting

## Advanced Examples

### Conditional Formatting
You can create more complex formats that work well with or without prefixes:

```yaml
# This works whether the player has a prefix or not
chat_prefix: "<gray>[<blue>{server}</blue>]</gray> {luckperms_prefix}<white><bold>{player}</bold>:</white> "

# Results:
# With prefix: [Survival] [VIP] PlayerName: message
# Without prefix: [Survival] PlayerName: message
```

### Custom Spacing
```yaml
# Add space only if prefix exists (automatic)
chat_prefix: "{luckperms_prefix}<white><bold>{player}</bold>:</white> "

# Results:
# With prefix: [Admin] PlayerName: message
# Without prefix: PlayerName: message
```

## Troubleshooting

### Prefixes Not Showing
1. Check if LuckPerms is properly installed
2. Verify players have prefixes set in LuckPerms
3. Check server logs for LuckPerms integration warnings
4. Test with `/discord status` to ensure plugin is working

### Formatting Issues
1. Ensure MiniMessage syntax is correct in your prefixes
2. Check that `{luckperms_prefix}` is spelled correctly
3. Verify the placeholder is in the right position in your format string

### Discord Formatting
1. Prefixes are automatically converted to plain text for Discord
2. Discord doesn't support Minecraft color codes
3. Complex formatting may not translate perfectly to Discord

## Migration from Old Format

If you're updating from a previous version:

**Old format**:
```yaml
chat_prefix: "<gray>[<blue>{server}</blue>]</gray> <white><bold>{player}</bold>:</white> "
```

**New format**:
```yaml
chat_prefix: "<gray>[<blue>{server}</blue>]</gray> {luckperms_prefix}<white><bold>{player}</bold>:</white> "
```

Simply add `{luckperms_prefix}` where you want the rank to appear!

## Complete Example Configuration

```yaml
discord:
  token: "YOUR_BOT_TOKEN"
  channel: "YOUR_CHANNEL_ID"
  message_format: "<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}"

messages:
  join: "<green>✅ {luckperms_prefix}<bold>{player}</bold> joined the network!</green>"
  leave: "<red>❌ {luckperms_prefix}<bold>{player}</bold> left the network!</red>"
  server_switch: "<yellow>🔄 {luckperms_prefix}<bold>{player}</bold> switched from <italic>{from}</italic> to <italic>{to}</italic></yellow>"
  chat_prefix: "<gray>[<blue>{server}</blue>]</gray> {luckperms_prefix}<white><bold>{player}</bold>:</white> "
```

This provides a complete rank-aware messaging system across your entire network!
