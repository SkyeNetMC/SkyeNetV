# Configuration Update Summary

## Changes Made

### 1. File ### 5. LuckPerms Integration### 7. Configuration StepsFeatures

The plugin now includes comprehensive LuckPerms prefix support:
- **Automatic prefix detection** from LuckPerms user metadata
- **Plain text conversion** for Discord compatibility
- **Fallback handling** when LuckPerms is not available
- **MiniMessage support** for rich formatting in prefixes
- **Network-wide consistency** across all message types

### 6. Activity Featuresenamed
- **Old**: `discord_config.yml`
- **New**: `config.yml`

### 2. New Bot Activity Configuration Added

The configuration now includes bot activity settings that update every 5 minutes:

```yaml
bot-activity:
  # Valid Types: ONLINE, DO_NOT_DISTURB, IDLE, INVISIBLE
  status: ONLINE
  # Valid Types: PLAYING, STREAMING, LISTENING, WATCHING, COMPETING, CUSTOM_STATUS
  type: CUSTOM_STATUS
  # Valid placeholders are %online% and/or %max-players% (UPDATES EVERY 5 MINUTES)
  text: '%online%/%max-players% - play.skyenet.co.in'
```

### 3. New LuckPerms Prefix Integration

Added support for `{luckperms_prefix}` placeholder in all message formats:

```yaml
messages:
  chat_prefix: "<gray>[<blue>{server}</blue>]</gray> {luckperms_prefix}<white><bold>{player}</bold>:</white> "
  join: "<green>✅ {luckperms_prefix}<bold>{player}</bold> joined the network!</green>"
  leave: "<red>❌ {luckperms_prefix}<bold>{player}</bold> left the network!</red>"
```

### 4. Code Changes

#### DiscordConfig.java
- Updated to load from `config.yml` instead of `discord_config.yml`
- Added bot activity configuration fields and getters
- Improved `isConfigured()` method to detect multiple placeholder patterns
- Updated file name references in logs and error messages

#### PrefixUtils.java
- Added `getPlayerPrefixText()` method to extract LuckPerms prefix as plain text
- Added PlainTextComponentSerializer for Discord compatibility

#### DiscordManager.java
- Added bot activity functionality with automatic updates every 5 minutes
- Added support for different activity types (PLAYING, WATCHING, etc.)
- Added support for different online statuses (ONLINE, DO_NOT_DISTURB, etc.)
- Placeholder replacement for `%online%` and `%max-players%`
- Added `{luckperms_prefix}` placeholder support in all message methods
- Proper shutdown handling for the activity scheduler

#### SkyeNetV.java
- Updated configuration file name references
- Updated log messages to reflect broader configuration scope

#### DiscordCommand.java
- Updated permission error messages
- Updated reload success messages

### 4. Activity Features

The bot will now:
- Display current player count and max players in its status
- Update the activity every 5 minutes automatically
- Support different activity types:
  - `PLAYING` - "Playing [text]"
  - `WATCHING` - "Watching [text]"
  - `LISTENING` - "Listening to [text]"
  - `STREAMING` - "Streaming [text]"
  - `COMPETING` - "Competing in [text]"
  - `CUSTOM_STATUS` - Just displays the text

- Support different online statuses:
  - `ONLINE` - Green dot
  - `DO_NOT_DISTURB` - Red dot
  - `IDLE` - Yellow dot
  - `INVISIBLE` - Offline appearance

### 5. Configuration Steps

To use the new features:

1. **Replace your old `discord_config.yml`** with your actual bot token and channel ID in the new `config.yml`
2. **Customize bot activity** by editing the `bot-activity` section
3. **Configure LuckPerms prefixes** using the `{luckperms_prefix}` placeholder
4. **Restart your server** to apply changes
5. **Use `/discord status`** to verify the connection

### 8. Example Complete Configuration

```yaml
# SkyeNetV Configuration
discord:
  token: "YOUR_ACTUAL_BOT_TOKEN_HERE"
  channel: "YOUR_ACTUAL_CHANNEL_ID_HERE"
  show_prefixes: true
  enable_join_leave: true
  enable_server_switch: true
  only_global_chat_to_discord: true
  disabled_servers:
    - "lobby"
    - "hub"
  name_format: "username"
  message_format: "<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}"

bot-activity:
  status: ONLINE
  type: CUSTOM_STATUS
  text: '%online%/%max-players% - play.skyenet.co.in'

network:
  broadcast_join_to_all_servers: true
  broadcast_leave_to_all_servers: true
  show_server_transfers: false
  join_format: "<green>✅ <bold>{player}</bold> joined the network!</green>"
  leave_format: "<red>❌ <bold>{player}</bold> left the network!</red>"

messages:
  join: "<green>✅ {luckperms_prefix}<bold>{player}</bold> joined the network!</green>"
  leave: "<red>❌ {luckperms_prefix}<bold>{player}</bold> left the network!</red>"
  server_switch: "<yellow>🔄 {luckperms_prefix}<bold>{player}</bold> switched from <italic>{from}</italic> to <italic>{to}</italic></yellow>"
  chat_prefix: "<gray>[<blue>{server}</blue>]</gray> {luckperms_prefix}<white><bold>{player}</bold>:</white> "
```

## Summary

The configuration system has been enhanced with:
- **Bot activity status updates** every 5 minutes
- **LuckPerms prefix integration** with `{luckperms_prefix}` placeholder
- **Broader configuration scope** (renamed from discord_config.yml to config.yml)
- **Better placeholder validation** to prevent initialization with invalid tokens
- **More robust error handling and logging**

The compiled plugin is ready in `target/SkyeNetV-2.4.3.jar`!
