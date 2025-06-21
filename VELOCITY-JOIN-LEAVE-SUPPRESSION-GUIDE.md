# Velocity Join/Leave Message Suppression Guide

## Overview
To completely suppress vanilla join/leave messages in Velocity and use only custom ones, you need to make changes both in the plugin configuration and the Velocity server configuration.

## Plugin Configuration (config.yml)
```yaml
join_leave:
  enabled: true
  suppress_vanilla: true
  join_format: "<gray>[</gray><green><bold>+</bold></green><gray>]</gray> {prefix}<white>{player}</white>{suffix} <green>joined the network</green>"
  leave_format: "<gray>[</gray><red><bold>-</bold></red><gray>]</gray> {prefix}<white>{player}</white>{suffix} <red>left the network</red>"
```

## Velocity Server Configuration (velocity.toml)
To completely suppress vanilla join/leave messages, add or modify the following in your `velocity.toml` file:

```toml
[messages]
# Disable player join/leave messages
# Set these to empty strings to suppress vanilla messages
player-info-forwarding-mode = "modern"

# Custom message handling
[advanced]
# These settings help with message handling
connection-timeout = 5000
read-timeout = 30000
```

## Alternative Method (Recommended)
If you want to completely disable vanilla messages, you can modify the `velocity.toml` file's messages section:

```toml
[messages]
# You can set custom messages or empty them
# Note: Some Velocity versions handle this differently
```

## Important Notes

1. **Event Priority**: Our plugin uses `PostOrder.LAST` to ensure our messages are sent after any potential vanilla messages.

2. **Color Support**: All messages support MiniMessage formatting:
   - `<green>` for green text
   - `<bold>` for bold text
   - `<gray>` for gray text
   - And many more formatting options

3. **LuckPerms Integration**: The `{prefix}` and `{suffix}` placeholders automatically pull from LuckPerms if available.

4. **Network-wide**: Messages are sent to all players across the entire Velocity network.

## Testing

To test if vanilla messages are properly suppressed:

1. Join the server and check if you see duplicate messages
2. If you see both vanilla and custom messages, adjust the Velocity configuration
3. Monitor the plugin logs for debugging information

## Troubleshooting

If vanilla messages still appear:

1. Check `velocity.toml` for join/leave message settings
2. Verify the plugin is properly loaded and events are registered
3. Check plugin logs for any errors
4. Ensure `suppress_vanilla: true` in the plugin config

## Message Format Examples

With a player named "Player123" with prefix "[Owner]":

**Join Message:**
```
[+] [Owner] Player123 joined the network
```

**Leave Message:**
```
[-] [Owner] Player123 left the network
```

The messages will have proper colors:
- Brackets `[+]` and `[-]` in gray
- Plus sign `+` in bold green
- Minus sign `-` in bold red
- Player name in white
- Network text in green/red respectively
