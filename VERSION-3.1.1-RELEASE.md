# SkyeNetV Version 3.1.1 - Global Chat & Messaging Fixes

## 🎯 Release Summary
Version 3.1.1 addresses critical issues with global chat message visibility and cross-server private messaging functionality. This release ensures a seamless communication experience across your Velocity network.

## 🔧 Critical Bug Fixes

### Global Chat Sender Visibility Fix
- **Issue**: When sending global chat messages, the sender couldn't see the globe emoji (🌐) on their own messages
- **Fix**: Globe emoji now appears on sender's own messages when global chat is enabled
- **Impact**: Improved user experience and confirmation that messages were sent to global chat

### Cross-Server Private Messaging Fix
- **Issue**: `/msg` and `/r` commands only worked for players on the same server
- **Fix**: Enhanced player lookup to find players across all servers in the network
- **Impact**: Private messaging now works seamlessly between players on different servers

## ✨ Enhancements

### Enhanced Player Search
- **Exact Name Matching**: Prioritizes exact username matches (case-insensitive)
- **Partial Name Matching**: Falls back to partial name matching for convenience
- **Cross-Server Lookup**: Searches all connected players across the entire network

### Improved Error Messages
- **Clear Player Not Found**: Better error messages when players cannot be located
- **Self-Message Prevention**: Prevents sending messages to yourself with humorous error
- **Offline Player Handling**: Clear messaging when reply targets are no longer online

### Infrastructure Improvements
- **Lobby Command Preparation**: Added configuration support for lobby teleportation coordinates
- **Join/Leave Message Fixes**: Resolved Component handling compilation issues
- **Enhanced Configuration**: Added lobby spawn coordinate configuration methods

## 📋 Technical Details

### Modified Files
- **GlobalChatManager.java**: Updated `sendGlobalMessage()` to include sender in recipients
- **PrivateMessageCommand.java**: Enhanced with cross-server player search functionality
- **JoinLeaveListener.java**: Fixed Component to String conversion issues
- **Config.java**: Added lobby coordinate configuration methods
- **CHANGELOG.md**: Updated with comprehensive change documentation

### Key Code Changes
```java
// Global Chat - Now includes sender in message recipients
for (Player player : server.getAllPlayers()) {
    player.sendMessage(globalMessage); // Now includes sender
}

// Private Messages - Enhanced player search
private Player findPlayer(String name) {
    // Exact match first, then partial match across all servers
    for (Player player : server.getAllPlayers()) {
        if (player.getUsername().equalsIgnoreCase(name)) {
            return player;
        }
    }
    // Fallback to partial matching...
}
```

## 🚀 Installation Instructions

### For New Installations
1. Download `SkyeNetV-3.1.1.jar` from the target directory
2. Place in your Velocity proxy's `plugins` folder
3. Start your Velocity proxy
4. Configure `config.yml` as needed

### For Existing Installations
1. Stop your Velocity proxy
2. Replace the old SkyeNetV JAR with `SkyeNetV-3.1.1.jar`
3. Start your Velocity proxy
4. No configuration changes required - all existing settings preserved

## 🧪 Testing Recommendations

### Global Chat Testing
1. Enable global chat with `/gc`
2. Send a message - verify you see the globe emoji (🌐) on your own message
3. Have players on different servers confirm they receive the message with globe emoji
4. Disable global chat with `/gc` - verify messages stay local

### Private Messaging Testing
1. Test `/msg <player>` between players on different servers
2. Test partial name matching (e.g., `/msg pil` for player "PilkeySEK")
3. Test reply functionality with `/r` across servers
4. Verify error messages for offline/invalid players

### Cross-Server Functionality
1. Have players join different servers in your network
2. Test all communication features between servers
3. Verify join/leave messages display correctly
4. Test Discord integration if enabled

## 📊 Performance Impact
- **Minimal**: Changes only affect message processing and player lookup
- **Optimized**: Player search uses efficient iteration over connected players
- **Scalable**: Maintains performance with large player counts

## 🔄 Backward Compatibility
- **Full Compatibility**: All existing configurations and features preserved
- **No Breaking Changes**: Existing installations will work without modification
- **Enhanced Functionality**: All previous features work better with these fixes

## 🛠️ Troubleshooting

### If Global Chat Messages Don't Show Globe Emoji
- Verify global chat is enabled with `/gc`
- Check that `global_chat.enabled` is `true` in config.yml
- Restart proxy if issues persist

### If Private Messages Don't Work Cross-Server
- Verify both players are online and connected to the proxy
- Check that players have `skyenetv.msg` permission
- Test with exact usernames first, then partial names

### If Join/Leave Messages Cause Errors
- Check proxy logs for Component-related errors
- Verify LuckPerms is properly installed and configured
- Ensure `join_leave.enabled` is `true` in config.yml

## 🎉 What's Next?
Version 3.1.1 completes the core messaging functionality. Future releases will focus on:
- Lobby teleportation system implementation
- Advanced Discord integration features
- Performance optimizations for large networks
- Additional customization options

---

**File**: `target/SkyeNetV-3.1.1.jar`  
**Build Date**: June 20, 2025  
**Compatibility**: Velocity 3.1.0+, Java 17+  
**Dependencies**: LuckPerms (optional, for prefix/suffix support)
