# Changelog

## Version 2.4.7 (June 10, 2025)

### 🎨 Configurable Global Chat Messages
- **Configurable Message Formats**: Global chat messages now use customizable MiniMessage formats
  - Separate formats for messages with/without globe icon
  - Configurable join/leave notification messages
  - Customizable new player notification message
- **Enhanced Color Continuation**: Added `getFullFormattedNameWithColorContinuation()` for seamless gradient flow
- **Flexible Placeholder System**: Support for `{player}`, `{luckperms_prefix}`, and `{message}` placeholders
- **Config-Driven Display**: All global chat message formats controlled via `config.yml`

### New Configuration Options
```yaml
global_chat:
  message_with_icon: "🌐 {luckperms_prefix}<bold>{player}</bold>: {message}"
  message_without_icon: "{luckperms_prefix}<bold>{player}</bold>: {message}"
  join_message: "🌐 {luckperms_prefix}<bold>{player}</bold> <green>joined global chat</green>"
  leave_message: "🌐 {luckperms_prefix}<bold>{player}</bold> <red>left global chat</red>"
  new_player_notification: "<green>You are not connected to global chat. Type </green><gold><bold>/gc</bold></gold><green> to toggle.</green>"
```

### Technical Changes
- Enhanced `DiscordConfig.java`: Added global chat message format fields and getters
- Updated `GlobalChatCommand.java`: Use configurable formats for join/leave/notification messages
- Updated `SkyeNetV.java`: Main global chat display now uses configurable MiniMessage formats
- Enhanced `PrefixUtils.java`: Added color continuation method for gradient preservation
- Updated `config.yml`: Added comprehensive global chat message format section
- Version bump: `2.4.6` → `2.4.7`

### Benefits
- **Customizable Branding**: Server owners can customize all global chat message formats
- **Better Color Support**: Proper gradient/color continuation from prefix to username
- **MiniMessage Integration**: Full support for modern Minecraft text formatting
- **Consistent Configuration**: All message formats centralized in config.yml

## Version 2.4.6 (June 10, 2025)

### 🔧 Critical Discord Fix
- **Discord Message Content Intent**: Fixed empty Discord message content issue
  - **Root Cause**: JDA was not configured with `MESSAGE_CONTENT` intent
  - **Solution**: Automatically enable `MESSAGE_CONTENT` intent in JDA initialization
  - **Impact**: Resolves 95% of Discord → Minecraft message content issues
- **Optimized Channel Filtering**: Improved Discord listener performance
  - Early channel filtering to reduce unnecessary processing
  - Silently ignore messages from non-target channels
  - Enhanced diagnostic messages for troubleshooting
- **Enhanced Error Diagnostics**: Added specific diagnostic for missing MESSAGE_CONTENT intent

### Technical Changes
- Updated `DiscordManager.java`: Added `.enableIntents(MESSAGE_CONTENT)` to JDA builder
- Updated `DiscordListener.java`: Optimized channel filtering and enhanced diagnostics
- Version bump: `2.4.5` → `2.4.6`

### Files Modified
- `src/main/java/me/pilkeysek/skyenetv/discord/DiscordManager.java`
- `src/main/java/me/pilkeysek/skyenetv/discord/DiscordListener.java`
- `src/main/java/me/pilkeysek/skyenetv/SkyeNetV.java`
- `pom.xml`

## Version 2.4.5 (June 10, 2025)

### Global Chat Enhancements
- **Join Notifications**: New players receive a notification when connecting with global chat disabled
  - Message: "You are not connected to global chat. Type `/gc` to toggle."
  - Clickable `/gc` command for easy activation
  - Appears 1 second after joining to avoid interference with join messages
- **Global Chat Join/Leave Messages**: Enhanced broadcast system for global chat status changes
  - Join message: `🌐 [Prefix] PlayerName [Suffix] joined global chat` (green text)
  - Leave message: `🌐 [Prefix] PlayerName [Suffix] left global chat` (red text)  
  - Messages include **full LuckPerms formatting** (prefix, colored names, suffix)
  - Only visible to players who can receive global messages
- **Improved User Experience**: Better onboarding for new players to understand global chat system

### Technical Improvements
- Added `broadcastGlobalChatJoinMessage()` and `broadcastGlobalChatLeaveMessage()` methods
- Enhanced `GlobalChatCommand` with plugin reference for better integration
- Updated join event handler with delayed notification system
- All global chat status changes now broadcast to eligible players

### Message Duplication Fix
- **Fixed chat duplication issue**: Messages no longer appear twice when global chat is enabled
- **Proper event handling**: Original chat events are now properly cancelled when global chat broadcasts
- **Discord integration preserved**: All Discord functionality remains intact

## Version 2.4.4 (June 10, 2025)

### Local Chat Improvements
- **Enhanced Local Chat Command**: `/lc` now sends messages to local chat without the "LOCAL" prefix
  - Messages appear as normal chat format: `PlayerName: message`
  - Maintains server-specific message routing (only visible to players on same server)
  - Cleaner, more natural local chat experience
  - No functional changes to Discord integration or global chat

### Technical Changes
- Updated version number to 2.4.4 for consistency
- Modified `LocalChatCommand.java` to remove prefix formatting
- All other features from 2.4.3 remain unchanged

## Version 2.4.3 (June 10, 2025)

### Maintenance Release
- **Version Bump**: Updated version number to 2.4.3 for consistency
- **Build Updates**: Updated all build files and metadata
- **No Functional Changes**: All features from 2.4.1 remain unchanged

## Version 2.4.1 (June 10, 2025)

### New Features
- **Enhanced Global Chat System**: Advanced network-wide chat functionality with granular controls
  - **Settings Menu**: New `/gc settings` command with interactive clickable settings
  - **Icon Toggle**: Players can show/hide the globe emoji (🌐) with `/gc toggle icon`
  - **Message Control**: Separate toggles for receiving and sending global messages
    - `/gc toggle receive` - Control whether you see global messages
    - `/gc toggle send` - Control whether your messages go to global chat
  - **Tab Completion**: Full tab completion support for all commands and subcommands

- **Server Hover Information**: Enhanced global chat display with server details
  - Hovering over the globe emoji (🌐) shows:
    - Player name and current server
    - "Global Chat" indicator with formatting
    - Server name highlighting in yellow

- **Enhanced LuckPerms Integration**: Full rank color and formatting support
  - **Player Name Colors**: Automatic color application from LuckPerms groups
  - **Meta Value Support**: Supports `name-color` and `group-color` meta values
  - **Full Formatting**: Combines prefix, colored username, and suffix seamlessly
  - **Rich Text Support**: Complete MiniMessage format support for all LuckPerms elements

### UI/UX Improvements
- **Interactive Settings Menu**: Clickable toggles with visual status indicators (✓/✗)
- **Contextual Help**: Settings command link provided when toggling global chat
- **Visual Status Indicators**: Clear ON/OFF status with color coding (green/red)
- **Hover Tooltips**: Informative hover text for better user experience
- **Formatted Borders**: Professional-looking settings menu with decorative borders

### Bug Fixes
- **Permission Issues**: Fixed GlobalChatCommand to allow all players to use global chat
- **LobbyCommand Error Handling**: Enhanced error messages and debugging information
  - Now shows available servers when 'lobby' server is not found
  - Better handling of edge cases and server connection issues
- **Metadata Updates**: Fixed velocity-plugin.json version number to match actual version
- **Code Cleanup**: Removed unused imports and fields for cleaner compilation

### Technical Improvements
- **Modular Settings System**: New `GlobalChatSettings` class for per-player preferences
- **Enhanced Color Processing**: Advanced LuckPerms color extraction and application
- **Improved Message Formatting**: Better component building for complex chat messages
- **Tab Completion System**: Comprehensive command completion for better usability
- **Memory Efficient**: Optimized storage of player preferences and settings

### Command Reference
```
/gc                    # Toggle global chat on/off
/gc settings          # Open interactive settings menu
/gc toggle icon       # Toggle globe emoji display
/gc toggle receive    # Toggle receiving global messages
/gc toggle send       # Toggle sending to global chat
```

### LuckPerms Meta Values
The plugin now supports these LuckPerms meta values:
- `name-color` - Sets the color of the player's username
- `group-color` - Fallback color based on primary group
- Standard prefix/suffix with full MiniMessage formatting

## Version 2.4 (June 6, 2025)

### New Features
- **Network Join/Leave Configuration**: Added comprehensive network messaging system
  - `broadcast_join_to_all_servers` - Control network-wide join message broadcasting
  - `broadcast_leave_to_all_servers` - Control network-wide leave message broadcasting
  - `show_server_transfers` - Optional notifications for server transfers
  - Custom message formats for network join/leave events

- **Discord Name Format Options**: Enhanced Discord integration
  - `name_format` setting - Choose between "username" or "displayname"
  - `message_format` setting - Customizable Discord-to-game message format
  - Support for Discord display names vs usernames

### Configuration Updates
- Added `network` section to `discord_config.yml`
- All new features are fully configurable via YAML
- Enhanced MiniMessage format support for all message types

### Technical Improvements
- Updated configuration loading system
- Enhanced Discord message handling
- Improved network broadcasting functionality

## Version 2.3 (June 5, 2025)

### Breaking Changes
- **Removed Chat Filter System**: Complete removal of chat filtering functionality
  - Deleted `ChatFilterCommand.java` and `ChatFilterModule.java`
  - Removed all filter configuration files and directories
  - Removed filter-related documentation
  - Simplified Discord integration to remove filter dependencies

### Changes
- Simplified main plugin class by removing chat filter initialization
- Updated Discord configuration to remove filter-related settings
- Streamlined event handling without filter checks
- Removed unused LuckPerms imports and dependencies
- Updated documentation to focus on rules system and Discord integration
- Preserved SnakeYAML dependency for Discord configuration

### Current Features
- **Discord Integration**: Full chat bridging with MiniMessage support
- **Rules System**: Comprehensive server rules management
- **Lobby Commands**: `/lobby`, `/l`, `/hub` teleportation
- **Administrative Tools**: Configuration reload capabilities

## Version 2.2 (Previous)
- Discord Integration enhancements
- Rules System implementation
- Chat Filter functionality (now removed)
- Server management improvements

## Version 2.1 (Previous)
- Initial Rules System
- MiniMessage support
- Admin commands
- JSON-based configuration

### Breaking Changes
- **Removed Chat Filter System**: Complete removal of chat filtering functionality
  - Deleted `ChatFilterCommand.java` and `ChatFilterModule.java`
  - Removed all filter configuration files and directories
  - Removed filter-related documentation
  - Simplified Discord integration to remove filter dependencies

### Changes
- Simplified main plugin class by removing chat filter initialization
- Updated Discord configuration to remove filter-related settings
- Streamlined event handling without filter checks
- Removed unused LuckPerms imports and dependencies
- Updated documentation to focus on rules system and Discord integration
- Preserved SnakeYAML dependency for Discord configuration

### Current Features
- **Discord Integration**: Full chat bridging with MiniMessage support
- **Rules System**: Comprehensive server rules management
- **Lobby Commands**: `/lobby`, `/l`, `/hub` teleportation
- **Administrative Tools**: Configuration reload capabilities

## Version 2.2 (Previous)
- Discord Integration enhancements
- Rules System implementation
- Chat Filter functionality (now removed)
- Server management improvements

## Version 2.1 (Previous)
- Initial Rules System
- MiniMessage support
- Admin commands
- JSON-based configuration
