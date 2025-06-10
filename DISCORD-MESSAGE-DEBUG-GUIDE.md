# Discord Message Content Debug Guide

## Issue Description
Discord integration shows `[Discord] Username:` but not the actual message content when messages are sent from Discord to Minecraft.

## Debug Version Created
The plugin has been compiled with enhanced debug logging to identify the root cause of this issue.

## Steps to Debug

### 1. Install Debug Version
Replace your current plugin with the newly compiled version:
```
target/SkyeNetV-2.4.3.jar
```

### 2. Enable Debug Logging
Add these lines to your Velocity configuration to see detailed debug output:

**Option A: In velocity.toml**
```toml
[advanced]
log-level = "INFO"
```

**Option B: Enable specific Discord debugging**
The plugin will automatically log detailed information at INFO level for Discord messages.

### 3. Test Discord → Minecraft Message Flow

1. **Start your server** with the debug version
2. **Send a message from Discord** to your configured channel
3. **Check the server logs** for detailed debug output

### Expected Debug Output
You should see log entries like this:

```
[INFO] Received Discord message from TestUser: Hello from Discord!
[INFO] Discord message details:
[INFO]   - author name: 'TestUser'
[INFO]   - display name: 'TestUser Display'
[INFO]   - message.getContentRaw(): 'Hello from Discord!'
[INFO]   - message.getContentDisplay(): 'Hello from Discord!'
[INFO]   - message.getContentStripped(): 'Hello from Discord!'
[INFO] Broadcasting Discord message to Minecraft players: [TestUser] Hello from Discord!
[INFO] broadcastDiscordMessage called with:
[INFO]   - author: 'TestUser'
[INFO]   - displayName: 'TestUser Display'
[INFO]   - message: 'Hello from Discord!'
[INFO]   - message length: 18
[INFO] Name to use: 'TestUser'
[INFO] Message format template: '<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}'
[INFO] Formatted message after replacement: '<gray>[Discord]</gray> <white><bold>TestUser</bold>:</white> Hello from Discord!'
[INFO] Sent Discord message to X online players
```

### 4. Analyze the Debug Output

**Case 1: Message content is empty**
If you see:
```
[INFO]   - message: ''
[INFO]   - message length: 0
```
This indicates Discord is sending empty messages. Check:
- Discord bot permissions (Read Message History, View Channel)
- Message content intents in Discord Developer Portal

**Case 2: Message content is correct but format is wrong**
If message content looks good but the formatted output is wrong, check:
- Configuration file syntax
- MiniMessage format validity

**Case 3: No debug output at all**
If you don't see any debug output:
- Verify the bot is connected (`/discord status`)
- Check if messages are from the correct channel
- Verify bot is not ignoring messages (check bot user status)

### 5. Common Solutions

**Solution A: Discord Bot Permissions**
Ensure your Discord bot has these permissions in the channel:
- View Channel
- Send Messages
- Read Message History
- Use External Emojis (optional)

**Solution B: Message Content Intent**
In Discord Developer Portal → Bot → Privileged Gateway Intents:
- Enable "Message Content Intent"

**Solution C: Configuration Issues**
Check your `config.yml` format:
```yaml
discord:
  message_format: "<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}"
```

### 6. Report Back
After testing with the debug version, share these details:

1. **Full debug output** from when you send a Discord message
2. **Your current configuration** (`config.yml` discord section)
3. **Discord bot permissions** in the channel
4. **Any error messages** in the logs

## Quick Fix Attempts

### Fix 1: Verify Configuration
Check that your `config.yml` has the correct message format:
```yaml
discord:
  message_format: "<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}"
```

### Fix 2: Restart with Clean Config
1. Stop server
2. Delete `plugins/SkyeNetV/config.yml`
3. Start server (regenerates config)
4. Configure Discord token and channel ID
5. Test again

### Fix 3: Discord Bot Setup
1. Go to Discord Developer Portal
2. Navigate to your bot → Bot
3. Enable "Message Content Intent"
4. Save changes
5. Restart your Discord bot

## Additional Commands for Testing

```bash
# Check Discord status
/discord status

# Send test message to Discord
/discord test

# Reload configuration
/discord reload
```

---

**Note**: This debug version includes extensive logging. Once the issue is resolved, we can provide a clean version without the debug output.
