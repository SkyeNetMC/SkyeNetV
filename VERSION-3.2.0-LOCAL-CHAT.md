# SkyeNetV Version 3.2.0 - Simplified Local Chat Focus

## Overview

As of version 3.2.0, SkyeNetV has been streamlined to focus exclusively on local chat functionality. Global chat capabilities have been completely removed, resulting in a simpler, more maintainable chat system that keeps all messages within their respective servers.

## Changes Made

1. **Removed Global Chat Functionality**
   - Removed GlobalChatManager class
   - Removed /gc and /lc commands
   - Eliminated global chat toggle states and related storage
   
2. **New ChatManager Implementation**
   - Created a new, simplified ChatManager class
   - All messages now stay local to their origin server
   - Maintained consistent chat formatting with LuckPerms integration
   - Preserved Discord message forwarding
   
3. **Simplified Architecture**
   - Removed complex message routing logic
   - Eliminated the need to check player toggle states
   - Removed redundant formatting code for different chat modes

4. **Updated Documentation**
   - Updated changelog
   - Created this transition document

## Benefits

1. **Simplified User Experience**
   - Players no longer need to manage chat modes
   - No confusion about which messages go where
   - No duplicate messages between servers
   
2. **Performance Improvements**
   - Reduced message broadcasting overhead
   - Eliminated per-player toggle state tracking
   
3. **Code Maintainability**
   - Reduced codebase complexity
   - Fewer edge cases to handle
   - Simpler testing and debugging

## Migration Guide for Server Admins

1. No configuration changes required
2. Update to version 3.2.0
3. Inform players that:
   - The /gc and /lc commands have been removed
   - All chat is now local to each server
   - Discord integration still works normally

## Technical Implementation Details

1. **ChatManager**:
   - Handles message formatting with LuckPerms prefixes/suffixes
   - Routes messages only to players on the same server
   - Forwards messages to Discord if configured

2. **Streamlined Event Handling**:
   - ChatListener now processes all messages through ChatManager
   - No conditional routing based on player state
   - Messages are always rendered with proper formatting

## Future Considerations

If global chat functionality is desired again in the future, it could be reimplemented as a standalone feature with a clearer separation of concerns. The current architecture makes it easy to add new chat types if needed.
