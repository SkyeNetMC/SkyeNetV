# Discord Integration Troubleshooting Guide

## Common Issue: Messages Only Flow Minecraft → Discord (Not Discord → Minecraft)

### Root Cause
The Discord listener was not being properly registered with the JDA (Java Discord API) instance due to timing issues during initialization.

### The Fix Applied
1. **Moved Discord listener registration**: The listener is now registered inside `DiscordManager.initialize()` after JDA is fully ready, rather than in the main plugin class.
2. **Enhanced error handling**: Added comprehensive logging and error handling throughout the Discord integration.
3. **Added diagnostic tools**: New Discord command subcommands for testing and status checking.

### Verification Steps

#### 1. Check Plugin Logs
Look for these log messages during startup:
```
[INFO] Initializing Discord bot with token: [REDACTED]...
[INFO] JDA initialized, looking for channel ID: [YOUR_CHANNEL_ID]
[INFO] Found Discord channel: [CHANNEL_NAME] (ID: [CHANNEL_ID])
[INFO] Discord listener initialized successfully!
[INFO] Test message sent to Discord successfully
[INFO] Discord bot initialized successfully!
```

#### 2. Configure Discord Properly
Edit `discord_config.yml`:
```yaml
discord:
  token: "YOUR_ACTUAL_BOT_TOKEN"
  channel: "YOUR_ACTUAL_CHANNEL_ID"
  # ... other settings
```

#### 3. Test Discord Integration
Use the new diagnostic commands:

**Check Discord Status:**
```
/discord status
```

**Send Test Message:**
```
/discord test
```

**Reload Configuration:**
```
/discord reload
```

#### 4. Required Discord Bot Permissions
Ensure your Discord bot has these permissions in the target channel:
- Read Messages
- Send Messages
- Read Message History
- Use External Emojis (optional, for formatted messages)

#### 5. Common Configuration Issues

**Wrong Channel ID:**
- The bot logs will show "Could not find Discord channel with ID: [ID]"
- Check available channels in the logs
- Get the correct channel ID (right-click channel → Copy ID)

**Invalid Bot Token:**
- The bot initialization will fail with authentication errors
- Generate a new token from Discord Developer Portal

**Bot Not in Server:**
- Ensure the bot is invited to your Discord server
- Check that it has access to the specified channel

### Testing the Fix

1. **Start the server** and check logs for successful Discord initialization
2. **Send a message from Discord** - it should appear in Minecraft chat with the format:
   ```
   [Discord] PlayerName: message content
   ```
3. **Send a message from Minecraft** - it should appear in Discord with server prefix
4. **Use `/discord test`** to send a test message to Discord
5. **Use `/discord status`** to verify connection status

### Advanced Debugging

If issues persist, enable debug logging by adding this to your logger configuration:
```yaml
logger:
  me.pilkeysek.skyenetv.discord: DEBUG
```

This will show detailed information about:
- Message reception from Discord
- Message processing and filtering
- Player broadcast attempts
- Error details

### Common Error Messages and Solutions

**"Discord chat channel is null, cannot process message"**
- The bot couldn't find the configured channel
- Verify channel ID in configuration
- Ensure bot has access to the channel

**"Message not from configured channel"**
- Messages from other channels are ignored (working as intended)
- Verify you're testing in the correct channel

**"Error broadcasting Discord message to Minecraft"**
- Check MiniMessage format in configuration
- Verify player permissions and network connectivity

### File Changes Made
- `DiscordManager.java`: Fixed listener registration timing
- `DiscordListener.java`: Enhanced error handling and logging
- `DiscordCommand.java`: Added test and status subcommands
- `SkyeNetV.java`: Added DiscordManager getter method
