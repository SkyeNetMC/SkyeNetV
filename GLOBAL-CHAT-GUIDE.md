# SkyeNetV Global Chat Features Guide

## Overview
SkyeNetV 2.4.1 introduces an advanced global chat system with granular controls, LuckPerms integration, and server information display.

## Global Chat Commands

### Basic Usage
```bash
/gc                    # Toggle global chat on/off
/gc settings          # Open interactive settings menu
```

### Advanced Controls
```bash
/gc toggle icon       # Show/hide the globe emoji (🌐)
/gc toggle receive    # Control if you see global messages
/gc toggle send       # Control if your messages go global
```

### Tab Completion
- All commands support tab completion
- Type `/gc ` and press Tab to see available options
- Works for subcommands and settings

## Settings Menu Features

### Interactive Settings
The `/gc settings` command opens a clickable menu with:

```
▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
🌐 Global Chat Settings

 • Global Chat: [✓ ON]           # Click to toggle
 • Show Globe Icon: [✓ ON]       # Click to toggle
 • Receive Global Messages: [✓ ON] # Click to toggle  
 • Send to Global Chat: [✓ ON]   # Click to toggle

▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
```

### Setting Descriptions

**Global Chat**: Master toggle for all global chat functionality
- ON: Enables global chat features
- OFF: Disables all global chat (local server only)

**Show Globe Icon**: Controls the 🌐 emoji display
- ON: Shows globe emoji with hover info
- OFF: Clean message format without icon

**Receive Global Messages**: Controls incoming global messages
- ON: You see messages from other servers
- OFF: Only see messages from your current server

**Send to Global Chat**: Controls outgoing message broadcasting
- ON: Your messages are sent to all servers
- OFF: Your messages stay on current server only

## Server Hover Information

### Globe Emoji Hover
When you hover over the 🌐 emoji in global chat messages, you see:

```
Global Chat
Server: lobby
Player: PlayerName
```

This helps you identify:
- Which server the message came from
- Who sent the message
- That it's a global chat message

## LuckPerms Integration

### Supported Features
- **Prefixes**: Full prefix display with formatting
- **Suffixes**: Complete suffix support
- **Name Colors**: Player username coloring
- **Group Colors**: Rank-based color schemes
- **MiniMessage**: Full rich text formatting support

### Meta Values
Set these in LuckPerms for enhanced formatting:

```bash
# Set player name color
/lp user PlayerName meta set name-color "red"
/lp user PlayerName meta set name-color "gradient:red:blue"

# Set group-based color
/lp group vip meta set group-color "gold"
/lp group admin meta set group-color "rainbow"
```

### Message Format
Global chat messages display as:
```
🌐 [Prefix]ColoredUsername[Suffix]: Message content
```

Example with LuckPerms formatting:
```
🌐 [VIP] PlayerName [★]: Hello everyone!
```

## Use Cases

### Different Player Preferences

**Player A** (Full Global Experience):
- Global Chat: ON
- Show Icon: ON  
- Receive: ON
- Send: ON
- Result: Sees and sends all global messages with icons

**Player B** (Receive Only):
- Global Chat: ON
- Show Icon: OFF
- Receive: ON
- Send: OFF
- Result: Sees global messages without icons, messages stay local

**Player C** (Clean Local Chat):
- Global Chat: OFF
- Result: Only sees local server chat, messages stay local

### Server Administrator
Use the settings to:
- Monitor global chat activity
- Test message broadcasting
- Troubleshoot user issues
- Verify LuckPerms integration

## Server Setup Requirements

### Basic Setup
1. Install SkyeNetV-2.4.1.jar in Velocity plugins folder
2. Configure multiple servers in velocity.toml
3. Restart Velocity proxy

### LuckPerms Setup (Optional)
1. Install LuckPerms on Velocity
2. Configure groups with prefixes/suffixes
3. Set meta values for enhanced colors
4. Restart to apply changes

### Verification
Test the system:
```bash
# Player 1 on server A
/gc                    # Enable global chat
Hello from server A!   # Should appear on all servers

# Player 2 on server B  
/gc settings          # Check settings menu works
/gc toggle icon       # Test icon toggle
Hello back!           # Should appear based on settings
```

## Troubleshooting

### Common Issues

**Commands not working**: See TROUBLESHOOTING.md
**No colors showing**: Verify LuckPerms is installed and configured
**Messages not broadcasting**: Check if players have global chat enabled
**Settings not saving**: Settings are per-session (reset on disconnect)

### Debug Commands
```bash
/gc settings          # Check current settings
/velocity plugins     # Verify plugin is loaded
/lp user <name> info  # Check LuckPerms permissions
```

## Advanced Configuration

### For Server Networks
- Set up consistent LuckPerms groups across servers
- Configure matching prefixes/suffixes
- Test cross-server message delivery
- Monitor performance with large player counts

### Performance Considerations
- Settings are stored in memory (lightweight)
- LuckPerms lookups are cached
- Message broadcasting scales with player count
- Consider limits for very large networks (1000+ players)

## Future Enhancements
Planned features for future versions:
- Persistent settings storage
- Channel-based global chat
- Message filtering and moderation
- Cross-server private messaging
- Global chat logging and analytics
