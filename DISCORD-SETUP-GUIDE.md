# Discord Setup Guide - Step by Step

## Current Issue
Your Discord integration is not working because the configuration still contains placeholder values. The plugin detects these and prevents Discord from initializing.

## Step-by-Step Fix

### 1. Create Discord Bot (if you haven't already)

1. Go to https://discord.com/developers/applications
2. Click "New Application" and give it a name (e.g., "SkyeNet Bot")
3. Go to the "Bot" section in the left sidebar
4. Click "Add Bot" if you haven't already
5. Under "Token", click "Copy" to copy your bot token
6. **Save this token securely - you'll need it next**

### 2. Get Your Discord Channel ID

1. In Discord, go to User Settings (gear icon)
2. Go to "Advanced" and enable "Developer Mode"
3. Right-click on the channel where you want messages to appear
4. Click "Copy ID"
5. **Save this channel ID - you'll need it next**

### 3. Invite Bot to Your Server

1. In the Discord Developer Portal, go to "OAuth2" → "URL Generator"
2. Under "Scopes", select "bot"
3. Under "Bot Permissions", select:
   - View Channels
   - Send Messages
   - Read Message History
   - Use External Emojis (optional)
4. Copy the generated URL and open it in your browser
5. Select your Discord server and authorize the bot

### 4. Update Configuration File

Edit your `config.yml` file and replace these lines:

**Replace this:**
```yaml
discord:
  token: "REPLACE_WITH_YOUR_ACTUAL_BOT_TOKEN"
  channel: "REPLACE_WITH_YOUR_ACTUAL_CHANNEL_ID"
```

**With this (using your actual values):**
```yaml
discord:
  token: "YOUR_ACTUAL_BOT_TOKEN_HERE"
  channel: "YOUR_ACTUAL_CHANNEL_ID_HERE"
```

**Example:**
```yaml
discord:
  token: "MTAwMTIzNDU2Nzg5MDEyMzQ1Ng.GaBcDe.fGhIjKlMnOpQrStUvWxYz1234567890"
  channel: "123456789012345678"
```

### 5. Restart Your Server

After updating the configuration, restart your Velocity proxy server.

### 6. Test the Integration

In Minecraft, run these commands to verify everything is working:

1. `/discord status` - Should show "CONNECTED"
2. `/discord test` - Should send a test message to Discord
3. Send a message in Discord - Should appear in Minecraft
4. Send a message in Minecraft - Should appear in Discord

## Troubleshooting

### If Discord status shows "NOT INITIALIZED":
- Check that you replaced the placeholder values in `config.yml`
- Restart your server after making config changes

### If Discord status shows "DISCONNECTED":
- Verify your bot token is correct
- Check that the bot is in your Discord server
- Verify the channel ID is correct
- Check server logs for error messages

### If messages don't appear:
- Make sure you're testing in the correct Discord channel
- Check that the bot has permission to read/send messages in that channel
- Verify global chat settings with `/gc settings`

## Current Configuration Status

Your current config has placeholder values that prevent Discord from initializing:
- Token: "REPLACE_WITH_YOUR_ACTUAL_BOT_TOKEN" ❌
- Channel: "REPLACE_WITH_YOUR_ACTUAL_CHANNEL_ID" ❌

Once you replace these with real values, Discord will work properly.

## Security Note

**Never share your bot token publicly!** It should be kept secret like a password.
