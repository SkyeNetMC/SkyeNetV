# Global Chat and Local Chat Features Guide - Updated

## New Features Added in This Version

### 1. Global Chat Discord Integration Control
Messages are now sent to Discord based on the player's global chat settings and server configuration.

### 2. Server-Specific Discord Disabling
Certain servers can be configured to never send messages to Discord, keeping all chat local.

### 3. Local Chat Command (`/lc`)
Players can send messages that only reach players on the same server, bypassing global chat and Discord.

## Configuration Options

### Discord Configuration (`discord_config.yml`)

```yaml
discord:
  # ... existing settings ...
  
  # Only send global chat messages to Discord (if false, all chat goes to Discord)
  only_global_chat_to_discord: true
  
  # List of servers where global chat is disabled and messages stay local
  disabled_servers:
    - "lobby"
    - "hub"
    # Add more server names as needed
```

## Commands

### Global Chat Commands (`/gc`)
- `/gc` - Toggle global chat on/off
- `/gc settings` - View current global chat settings
- `/gc toggle icon` - Toggle globe icon display
- `/gc toggle receive` - Toggle receiving global messages
- `/gc toggle send` - Toggle sending to global chat

### Local Chat Command (`/lc`)
- `/lc <message>` - Send a message only to players on the same server
- Aliases: `/localchat <message>`

### Discord Admin Commands
- `/discord status` - Check Discord connection and configuration
- `/discord test` - Send test message to Discord
- `/discord servers` - View disabled servers list
- `/discord reload` - Reload Discord configuration

## How It Works

### Message Flow Logic

1. **Player sends a chat message**
2. **Global Chat Check**: If player has global chat enabled and is sending messages:
   - Message is broadcast to all players with global chat receive enabled
   - Globe icon is shown (if enabled)
3. **Discord Integration Check**:
   - If server is in `disabled_servers` list → Skip Discord entirely
   - If `only_global_chat_to_discord` is `true`:
     - Only send to Discord if player has global chat enabled and is sending
   - If `only_global_chat_to_discord` is `false`:
     - Send all chat to Discord (original behavior)

### Local Chat (`/lc`)
- Messages sent with `/lc` only reach players on the same server
- Format: `[LOCAL] PlayerName: message`
- Never sent to Discord
- Never sent to global chat
- Useful for server-specific coordination

## Examples

### Scenario 1: Lobby Server
```yaml
disabled_servers:
  - "lobby"
```
- Players on lobby server chat normally
- Messages stay within lobby server only
- Nothing goes to Discord
- Global chat still works for inter-server communication

### Scenario 2: Global Chat Enabled Player
- Player has `/gc` enabled
- Messages go to all players with global chat receive enabled
- Messages also go to Discord (if server not disabled)

### Scenario 3: Global Chat Disabled Player
- Player has `/gc` disabled
- Messages only go to players on same server
- Messages don't go to Discord (if `only_global_chat_to_discord: true`)

### Scenario 4: Using Local Chat
- Player uses `/lc Hello everyone on this server!`
- Message shows as: `[LOCAL] PlayerName: Hello everyone on this server!`
- Only players on same server see it
- Never goes to Discord or global chat

## Admin Usage

### Check Discord Status
```
/discord status
```
Shows:
- Connection status
- Channel information
- Global chat only setting
- Disabled servers count

### Test Discord Connection
```
/discord test
```
Sends a test message to Discord to verify connectivity.

### View Disabled Servers
```
/discord servers
```
Lists all servers that have Discord disabled.

## Migration Notes

### For Existing Servers
- Default setting `only_global_chat_to_discord: true` means only global chat goes to Discord
- If you want all chat to go to Discord (old behavior), set it to `false`
- Add server names to `disabled_servers` list to completely disable Discord for those servers

### Permissions
- `skyenetv.discord.admin` - Access to test, status, and servers subcommands
- `skyenetv.discord.reload` - Access to reload subcommand
- No special permissions needed for global chat or local chat commands

## Troubleshooting

### Messages Not Going to Discord
1. Check if server is in `disabled_servers` list
2. Verify player has global chat enabled (`/gc settings`)
3. Check `only_global_chat_to_discord` setting
4. Use `/discord status` to verify connection

### Global Chat Not Working
1. Player must enable global chat with `/gc`
2. Player must enable sending with `/gc toggle send`
3. Recipients must enable receiving with `/gc toggle receive`

### Local Chat Issues
1. Verify all players are on the same server
2. Check server logs for any errors
3. Ensure LocalChatCommand is properly registered

## Configuration Examples

### Public Network Setup
```yaml
discord:
  only_global_chat_to_discord: true
  disabled_servers:
    - "lobby"
    - "hub"
    - "minigames"
```

### Private Network Setup
```yaml
discord:
  only_global_chat_to_discord: false  # All chat goes to Discord
  disabled_servers: []  # No disabled servers
```

### Hybrid Setup
```yaml
discord:
  only_global_chat_to_discord: true
  disabled_servers:
    - "creative"  # Keep creative server chat private
```
