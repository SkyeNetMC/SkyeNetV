# Discord Message Content Issue - Solution Guide

## Current Issue
Discord integration shows `[Discord] Username:` but the actual message content is missing when messages are sent from Discord to Minecraft.

## ✅ Debug Version Ready
A debug version has been compiled with extensive logging to identify the root cause: `target/SkyeNetV-2.4.3.jar`

## 🎯 Most Likely Causes & Solutions

### Solution 1: Discord Message Content Intent (Most Common)
**Issue**: Discord changed their API to require explicit permission for bots to read message content.

**Fix**:
1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Select your bot application
3. Navigate to **Bot** section
4. Scroll down to **Privileged Gateway Intents**
5. ✅ **Enable "Message Content Intent"**
6. Click **Save Changes**
7. **Restart your Minecraft server** (bot needs to reconnect)

### Solution 2: Empty Message Handling
**Issue**: Messages might be empty due to Discord formatting or special characters.

**Fix**: The debug version will show exactly what content is being received.

### Solution 3: Configuration Format Issue
**Issue**: Incorrect MiniMessage format in configuration.

**Fix**: Verify your `config.yml` has correct syntax:
```yaml
discord:
  message_format: "<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}"
```

## 🚀 Quick Testing Steps

### Step 1: Install Debug Version
```bash
# Replace your current plugin with the debug version
cp target/SkyeNetV-2.4.3.jar /path/to/your/server/plugins/
```

### Step 2: Test Discord → Minecraft
1. Start your server with the debug plugin
2. Send a simple message from Discord: `Hello test`
3. Check server logs for detailed output

### Step 3: Analyze Debug Output
Look for these log entries:

**Expected Success Pattern**:
```
[INFO] Received Discord message from TestUser: Hello test
[INFO] Discord message details:
[INFO]   - message: 'Hello test'
[INFO]   - message length: 10
[INFO] Formatted message after replacement: '<gray>[Discord]</gray> <white><bold>TestUser</bold>:</white> Hello test'
[INFO] Component as plain text: '[Discord] TestUser: Hello test'
[INFO] Sent Discord message to X online players
```

**Problem Patterns**:

**Pattern A - Empty Message**:
```
[INFO]   - message: ''
[INFO]   - message length: 0
```
→ **Solution**: Enable Message Content Intent (Solution 1)

**Pattern B - Null Message**:
```
[INFO]   - message: 'null'
```
→ **Solution**: Check bot permissions in Discord channel

**Pattern C - Format Issue**:
```
[INFO] Formatted message after replacement: '<gray>[Discord]</gray> <white><bold>TestUser</bold>:</white> '
```
→ **Solution**: Message content is missing from replacement

## 🔧 Advanced Troubleshooting

### Check Bot Permissions
Your Discord bot needs these permissions in the target channel:
- ✅ View Channel
- ✅ Send Messages  
- ✅ Read Message History
- ✅ Use External Emojis (optional)

### Verify Bot Setup
1. Check bot is online in Discord
2. Verify bot can see the target channel
3. Test bot responds to mentions
4. Confirm channel ID matches configuration

### Test Commands
```bash
# Check Discord connection status
/discord status

# Send test message to Discord
/discord test

# Reload configuration
/discord reload
```

## 📋 Information to Collect

If the issue persists after trying Solution 1, please share:

1. **Complete debug log output** when sending a Discord message
2. **Your configuration** (`config.yml` discord section)
3. **Discord bot permissions** screenshot
4. **Message Content Intent status** (enabled/disabled)
5. **Any error messages** in server logs

## 🏆 Expected Result

After applying Solution 1 (Message Content Intent), you should see:
- In Minecraft chat: `[Discord] Username: Your message content here`
- In server logs: Detailed debug information showing message content
- No more empty or missing message content

## 🧹 Clean Version

Once the issue is resolved, we can provide a clean version without the extensive debug logging.

---

**Quick Start**: Try Solution 1 first (Message Content Intent) - this fixes 90% of similar issues.
