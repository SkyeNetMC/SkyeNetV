# Changelog

## Version 3.2.1 (July 21, 2025) - ADMINISTRATION IMPROVEMENTS

### 🔧 Features and Improvements
- **Added Plugin Reload Command**: Implemented `/skyenetv reload` and `/snv reload` commands
- **Removed Discord Reload**: Removed the standalone `/discord reload` command
- **Simplified Configuration**: Updated config.yml with new reload command documentation
- **Improved Error Handling**: Better feedback for reload success/failure

## Version 3.2.0 (July 21, 2025) - SIMPLIFIED LOCAL CHAT FOCUS

### 🔄 Major Changes
- **Removed Global Chat**: Removed all global chat functionality for a simpler chat system
- **Simplified Chat System**: All messages now stay local to their respective servers
- **Code Cleanup**: Removed unused global chat toggle code and commands
- **Streamlined Architecture**: Replaced GlobalChatManager with streamlined ChatManager
- **Maintained Features**: LuckPerms prefix/suffix integration and Discord forwarding still work

### 🔧 Technical Changes
- **Replaced GlobalChatManager**: New ChatManager with simplified message routing
- **Removed Commands**: Removed /gc and /lc commands and related code
- **Preserved Formatting**: Maintained consistent chat formatting with LuckPerms
- **Simplified Configuration**: No more need for global chat toggle settings

## Version 3.1.2 (July 5, 2025) - CHAT DUPLICATION FIX

### 🔧 Critical Bug Fixes
- **FIXED: Chat Duplication for Senders**: Fixed issue where senders saw their own messages twice
- **FIXED: Message Format for Senders**: Senders now see their messages with proper formatting (including globe emoji)
- **FIXED: Message Routing Edge Cases**: Improved message routing logic to ensure proper delivery in all scenarios
- **FIXED: Self-Message Handling**: Players now see exactly one correctly formatted message for their own chat

## Version 3.1.1 (June 21, 2025) - GLOBAL CHAT SYSTEM REWRITE

### 🔧 Critical Bug Fixes
- **FIXED: Local Chat Duplication Bug**: Eliminated duplicate messages that appeared when global chat was toggled OFF
- **FIXED: Global Chat Message Routing**: Complete rewrite of message routing logic with explicit recipient handling
- **FIXED: Message Format Consistency**: Globe emoji (🌐) now consistently appears only for global chat messages
- **FIXED: Self-Message Handling**: Players now see their own messages with correct formatting
- **Fixed Global Chat Message Duplication**: Eliminated double messages when global chat is enabled
- **Fixed Global Chat Sender Visibility**: Globe emoji (🌐) now appears correctly on sender's own messages
- **Fixed Private Messaging Cross-Server**: `/msg` and `/r` commands now work properly across all servers in the network
- **Fixed Double Globe Emoji Issue**: Removed redundant globe emoji prefix causing double display
- **Implemented Channel-Based Global Chat**: Complete rewrite of global chat system to work as a proper channel

### 🌐 Global Chat Channel System (MAJOR UPDATE)
- **Mutually Exclusive Message Routing**: Completely redesigned routing logic to prevent duplicates
- **Two-Phase Message Processing**: Separate determination of recipients from message delivery
- **Format-Based Message Variants**: Different message formats created for different recipient types
- **Globe Emoji as Status Indicator**: 🌐 now indicates that the MESSAGE SENDER has global chat enabled
- **Recipient-Based Visibility**: Your ability to see cross-server messages depends on YOUR global chat setting
- **Smart Message Routing**: 
  - Global Chat ON: See messages from ALL servers (cross-server channel)
  - Global Chat OFF: See only messages from YOUR current server
- **Complete Backend Bypass**: All chat now processed through SkyeNet proxy, preventing duplication
- **Consistent Formatting**: All messages use SkyeNet formatting with LuckPerms integration

### 🌐 Global Chat Improvements
- **Proper Event Cancellation**: Global chat messages now properly cancel local chat events to prevent duplication
- **LuckPerms Integration Fix**: Now uses proper `PrefixUtils.getPrefixString()` and `PrefixUtils.getSuffixString()` methods
- **Consistent Message Format**: All recipients (including sender) see the same formatted global chat message
- **Single Message Path**: Streamlined message processing to eliminate race conditions

### 💬 Private Messaging Enhancements
- **Cross-Server Player Search**: Enhanced player lookup finds players by exact name or partial name matches across servers
- **Improved Error Messages**: More descriptive error messages when players are not found
- **Better Reply Functionality**: Reply system now works seamlessly across different servers
- **Self-Message Prevention**: Prevents sending messages to yourself with humorous error message

### 🔧 Technical Fixes
- **Consolidated Message Processing**: Removed redundant `handlePlayerMessage()` method to prevent double processing
- **Fixed Component Handling**: Resolved compilation issues with join/leave message Component handling
- **Enhanced Player Lookup**: Improved `findPlayer()` method with comprehensive cross-server search
- **Lobby Command Infrastructure**: Added configuration support for lobby teleportation coordinates

### 📋 Code Quality Improvements
- **Removed Placeholder Methods**: Eliminated unused `getLuckPermsPrefix()` placeholder in Config.java
- **Fixed ChatListener Syntax**: Cleaned up duplicate code and syntax errors in ChatListener.java
- **Early Event Processing**: Used `PostOrder.FIRST` to ensure global chat events are processed before backend servers

### 🛠️ Build & Deployment
- **Updated Version**: Incremented to 3.1.1 to reflect all bug fixes and improvements
- **Maven Build Success**: Plugin compiles cleanly with no errors or warnings
- **JAR Generation**: Successfully generates `SkyeNetV-3.1.1.jar` for deployment

### 🎯 What Was Fixed in Detail

#### Global Chat Message Flow (Before vs After)
**Before (3.1.0):**
```
1. Player sends message "test"
2. ChatListener processes message → calls processPlayerMessage()
3. sendGlobalMessage() adds extra 🌐 prefix
4. Message sent to all players: "🌐 🌐 [PREFIX] Player test"
5. Local server also processes message normally
6. Result: Double messages and double globe emojis
```

**After (3.1.1):**
```
1. Player sends message "test"
2. ChatListener processes message → calls processPlayerMessage()
3. sendGlobalMessage() uses config format directly (no extra prefix)
4. Message sent to all players: "🌐 [PREFIX] Player » test"
5. Original chat event is cancelled (event.setResult(denied()))
6. Result: Single message with single globe emoji
```

#### LuckPerms Integration (Before vs After)
**Before:**
- Used placeholder `config.getLuckPermsPrefix(player)` returning `"<prefix>"`
- Wrong placeholder replacement: `{luckperms_prefix}` (not in config format)

**After:**
- Uses proper `PrefixUtils.getPrefixString(player)` and `PrefixUtils.getSuffixString(player)`
- Correct placeholder replacement: `{prefix}` and `{suffix}` (matching config format)

### 🧪 Testing Results
- **Global Chat**: No more double messages ✅
- **Globe Emoji**: Single emoji display for all users ✅
- **LuckPerms Prefixes**: Proper prefix/suffix display ✅
- **Cross-Server Messaging**: Private messages work between servers ✅
- **Event Cancellation**: No local chat duplication ✅

## Version 3.1.0 (June 17, 2025) - DISCORD & GLOBAL CHAT RE-IMPLEMENTATION

### 🆕 Major Features Added Back
- **Discord Integration**: Complete Discord bot integration with configurable messages
- **Global Chat System**: Cross-server chat with customizable toggle behavior  
- **Comprehensive Configuration**: All messages, formats, and behavior now configurable

### 🌐 Global Chat Features
- `/gc` - Toggle global chat mode or send direct global messages
- `/lc` - Switch to local chat mode
- **Smart Message Handling**: When global chat is ON, shows message with globe emoji to sender, broadcasts to all servers, suppresses local
- **When Global Chat is OFF**: Normal local server chat behavior
- **Configurable default state** for new players
- **Custom message formats** with placeholder support

### 💬 Discord Integration
- **Bidirectional messaging** between Discord and game (configurable)
- **Global chat to Discord** relay with custom formats
- **Discord to game** messaging with custom formatting
- **Connection status monitoring** and status commands
- **Configurable bot token and channel settings**

### ⚙️ Configuration System
- **Main config.yml**: Complete configuration for all features
- **Backwards compatible** discord_config.yml fallback
- **Configurable message templates** using MiniMessage format
- **Feature toggles** for Discord integration aspects
- **Customizable join/leave message formats**

### 🎨 Message Customization
All command responses and system messages are now configurable:
- Global chat enable/disable messages
- Discord status messages  
- Join/leave message formats
- Error and permission messages
- Discord message formatting templates

### 🛠️ Technical Improvements
- **Smart config loading** with resource template fallbacks
- **MiniMessage support** for rich text formatting throughout
- **Improved error handling** and logging
- **Modular command system** with shared configuration
- **Memory-efficient** Discord connection management

### 📋 Command Updates
- `/gc [message]` - Toggle global chat or send global message
- `/lc` - Switch to local chat mode  
- `/discord [status]` - Show Discord integration info and status
- All commands now use configurable messages and permissions

### 🔧 Dependencies
- **JDA 5.0.0-beta.13** for Discord integration
- **SnakeYAML 2.0** for configuration management
- **MiniMessage 4.14.0** for rich text formatting (provided by Velocity)
- **LuckPerms API 5.4** for prefix/suffix integration (provided)

### 📦 Build Information
- **JAR Size**: ~47KB (increased from 24KB due to Discord/Global Chat features)
- **Java Version**: 17+
- **Velocity Version**: 3.1.1+

### 🔄 Migration Notes
- Existing v3.0.0 installations will automatically create new configuration files
- Old discord_config.yml files are still supported as fallback
- Join/leave message behavior unchanged from v3.0.0
- All core commands (rules, lobby, sudo) remain unchanged

## Version 3.0.0 (June 15, 2025) - LUCKPERMS JOIN/LEAVE MESSAGES

### ✅ NEW FEATURES
- **Custom Join/Leave Messages with LuckPerms Integration**
  - **Join Message Format**: `[+] [PREFIX] PlayerName [SUFFIX] joined the network` (green)
  - **Leave Message Format**: `[-] [PREFIX] PlayerName [SUFFIX] left the network` (red)
  - **LuckPerms Prefix/Suffix Support**: Full integration with player ranks and formatting
  - **Vanilla Message Replacement**: Disables default Velocity join/leave messages
  - **Network-wide Broadcasting**: Messages sent to all online players

### 🔧 TECHNICAL IMPLEMENTATION
- **PrefixUtils Enhancement**: Added `createJoinMessage()` and `createLeaveMessage()` methods
- **Event Handlers**: Added `PostLoginEvent` and `DisconnectEvent` listeners
- **LuckPerms Integration**: Full support for prefix, suffix, and color formatting
- **MiniMessage Support**: Proper parsing of LuckPerms formatting tags

### 📦 CURRENT PLUGIN FEATURES
- **Rules System**: `/rules` command with JSON configuration
- **Lobby Commands**: `/lobby`, `/l`, `/hub` for server navigation  
- **Administrative Tools**: `/sudo` command for admin tasks
- **Custom Join/Leave Messages**: LuckPerms-integrated network messages
- **LuckPerms Integration**: Prefix, suffix, and color support

### 🎨 MESSAGE EXAMPLES
```
[+] [ADMIN] PlayerName joined the network
[-] [VIP] PlayerName left the network
[+] [OWNER] PlayerName [★] joined the network
```

### 🔧 DEPENDENCIES
- **Velocity API**: Core proxy functionality
- **LuckPerms API**: Permission and formatting system
- **Adventure Text MiniMessage**: Text formatting and parsing

## Version 2.4.7 (June 10, 2025)

### 🚫 CRITICAL FIXES
- **FIXED: Global Chat Duplication Issue** - Messages no longer appear twice when global chat is enabled
  - Restructured chat event handler to properly cancel original events
  - Added early return after global chat processing to prevent duplicate handling
  - Fixed event.setResult(ChatResult.denied()) implementation

### 📝 NEW FEATURES
- **Updated Join/Leave Message Formats**
  - Join: `[+] [PREFIX] PlayerName joined global chat`
  - Leave: `[-] [PREFIX] PlayerName left global chat`
  - Clean gray brackets with green/red symbols for better readability

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
