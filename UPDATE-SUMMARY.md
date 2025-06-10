# SkyeNetV Global Chat Discord Integration Update

## Summary of Changes

This update adds advanced global chat Discord integration control and a new local chat system to SkyeNetV.

## New Features

### 1. Global Chat Discord Integration Control
- **Purpose**: Only send global chat messages to Discord, not all server chat
- **Configuration**: `only_global_chat_to_discord: true` in `discord_config.yml`
- **Behavior**: When enabled, only players with global chat active will have their messages sent to Discord

### 2. Server-Specific Discord Disabling
- **Purpose**: Completely disable Discord integration for specific servers
- **Configuration**: `disabled_servers:` list in `discord_config.yml`
- **Use Cases**: Keep lobby/hub chat local, prevent minigame spam in Discord

### 3. Local Chat Command (`/lc`)
- **Purpose**: Send messages only to players on the same server
- **Usage**: `/lc <message>` or `/localchat <message>`
- **Behavior**: Never goes to Discord or global chat, stays completely local

### 4. Enhanced Discord Admin Commands
- **`/discord status`**: Check connection status and configuration
- **`/discord test`**: Send test message to verify Discord connectivity
- **`/discord servers`**: View list of Discord-disabled servers
- **`/discord reload`**: Reload Discord configuration

## Files Modified

### Core Plugin Files
- **`SkyeNetV.java`**: Updated chat event handler with new Discord logic
- **`DiscordConfig.java`**: Added new configuration options and loading
- **`DiscordCommand.java`**: Added new admin subcommands
- **`DiscordManager.java`**: Enhanced with connection testing and status checking
- **`DiscordListener.java`**: Improved error handling and logging

### New Files
- **`LocalChatCommand.java`**: Implementation of `/lc` command
- **`GLOBAL-CHAT-DISCORD-GUIDE.md`**: Complete usage guide

### Configuration Files
- **`discord_config.yml`**: Updated with new settings and examples

## Configuration Options

### New Discord Settings
```yaml
discord:
  # Only send global chat messages to Discord (not all chat)
  only_global_chat_to_discord: true
  
  # Servers where Discord is completely disabled
  disabled_servers:
    - "lobby"
    - "hub"
    # Add more as needed
```

## Message Flow Logic

### Before (Old Behavior)
1. Player sends chat message
2. Message goes to Discord (if Discord enabled)
3. Message goes to global chat (if player has global chat enabled)

### After (New Behavior)
1. Player sends chat message
2. **Check server**: If server in `disabled_servers` → skip Discord entirely
3. **Check global chat setting**: 
   - If `only_global_chat_to_discord: true` → only send to Discord if player has global chat enabled
   - If `only_global_chat_to_discord: false` → send all chat to Discord (old behavior)
4. Message goes to global chat (if player has global chat enabled)

### Local Chat (`/lc`)
1. Player uses `/lc <message>`
2. Message only goes to players on same server
3. Never goes to Discord or global chat
4. Format: `[LOCAL] PlayerName: message`

## Use Cases

### Public Server Network
```yaml
discord:
  only_global_chat_to_discord: true
  disabled_servers:
    - "lobby"        # Keep lobby chat local
    - "hub"          # Keep hub chat local  
    - "minigames"    # Prevent spam from games
```

**Result**: Only meaningful cross-server communication goes to Discord

### Private Server Network
```yaml
discord:
  only_global_chat_to_discord: false
  disabled_servers: []
```

**Result**: All chat goes to Discord (original behavior)

### Hybrid Network
```yaml
discord:
  only_global_chat_to_discord: true
  disabled_servers:
    - "creative"     # Keep creative builds private
```

**Result**: Global chat goes to Discord except from creative server

## Commands Reference

### For Players
- **`/gc`**: Toggle global chat (existing)
- **`/gc settings`**: View global chat settings (existing)
- **`/gc toggle send`**: Control if messages go to global chat (existing)
- **`/lc <message>`**: Send local-only message (NEW)

### For Admins
- **`/discord status`**: Check Discord connection and settings (NEW)
- **`/discord test`**: Send test message to Discord (NEW)
- **`/discord servers`**: View disabled servers list (NEW)
- **`/discord reload`**: Reload Discord configuration (existing)

## Permissions

### New Permissions
- **`skyenetv.discord.admin`**: Access to status, test, and servers commands
- **`skyenetv.discord.reload`**: Access to reload command (unchanged)

### No Permission Required
- Global chat commands (`/gc`)
- Local chat command (`/lc`)

## Testing Scenarios

### Test 1: Global Chat to Discord
1. Player enables global chat: `/gc`
2. Player sends message: `Hello world!`
3. **Expected**: Message appears in Discord and to other global chat users

### Test 2: Local Chat Only
1. Player sends local message: `/lc Hello local server!`
2. **Expected**: Message only visible to players on same server, not Discord

### Test 3: Disabled Server
1. Configure `disabled_servers: ["lobby"]`
2. Player on lobby sends message: `Hello from lobby!`
3. **Expected**: Message stays on lobby server, never goes to Discord

### Test 4: Global Chat Disabled Player
1. Player disables global chat: `/gc` (to turn off)
2. Player sends message: `Hello!`
3. **Expected**: With `only_global_chat_to_discord: true`, message doesn't go to Discord

## Migration Guide

### For Existing Installations
1. **Backup** current `discord_config.yml`
2. **Update** plugin JAR to new version
3. **Restart** Velocity proxy
4. **Review** new `discord_config.yml` settings
5. **Configure** `disabled_servers` list as needed
6. **Test** Discord integration with `/discord status`

### Default Behavior
- New setting `only_global_chat_to_discord: true` by default
- Empty `disabled_servers` list by default
- Existing global chat functionality unchanged

## Build Information

### Compilation
- **Java Version**: 21
- **Maven Version**: 3.6.3
- **Build Status**: ✅ SUCCESS
- **Output**: `target/SkyeNetV-2.4.3.jar`

### Dependencies
- Velocity API
- JDA (Java Discord API)
- LuckPerms API (optional)
- Adventure API (for rich text)

## Deployment

### Installation
1. Stop Velocity proxy
2. Replace old SkyeNetV jar with `SkyeNetV-2.4.3.jar`
3. Update `discord_config.yml` with new settings
4. Start Velocity proxy
5. Test with `/discord status`

### Verification
- Check logs for successful Discord initialization
- Test global chat functionality
- Test local chat with `/lc`
- Verify Discord integration with configured settings

This update provides much more granular control over Discord integration while maintaining backward compatibility and adding useful new features for server administration.
