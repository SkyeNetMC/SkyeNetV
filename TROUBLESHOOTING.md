# SkyeNetV Troubleshooting Guide

## Command Not Working Issues

### `/lobby` or `/l` or `/hub` Command Issues

**Problem:** Command doesn't work or gives errors

**Common Solutions:**

1. **Check if 'lobby' server exists:**
   - The command looks for a server named exactly "lobby"
   - If your lobby server has a different name, you need to modify the LobbyCommand.java
   - The enhanced version now shows available servers when lobby is not found

2. **Server Configuration:**
   ```yaml
   # In your Velocity config.yml, ensure you have:
   servers:
     lobby:
       address: "localhost:25566"  # Your lobby server address
   ```

3. **Check Plugin Loading:**
   - Ensure SkyeNetV-2.4.1.jar is in your Velocity plugins folder
   - Check Velocity console for any plugin loading errors
   - Verify the plugin shows up in `/velocity plugins`

4. **Permission Issues:**
   - LobbyCommand has no permission requirements
   - Should work for all players by default

**Debugging Steps:**
1. Try the command - it will now show available servers if lobby doesn't exist
2. Check Velocity console for error messages
3. Verify server is running and accessible

### `/gc` or `/globalchat` Command Issues

**Problem:** Global chat command doesn't work

**Solutions:**

1. **Permission Fixed:**
   - Version 2.4.1 removed permission requirements
   - All players can now use `/gc` by default

2. **Check Plugin Registration:**
   - Ensure plugin loaded successfully
   - Check if other SkyeNetV commands work (like `/rules`)

3. **Test the Feature:**
   ```
   /gc                    # Toggle global chat on/off
   Chat in game          # Should broadcast to all servers (if enabled)
   ```

**Expected Behavior:**
- First `/gc`: Enables global chat, shows "🌐 [GlobalChat] Global chat enabled..."
- Second `/gc`: Disables global chat, shows "🌐 [GlobalChat] Global chat disabled..."
- When enabled: Your chat messages appear on all servers with 🌐 prefix

### General Command Issues

**All commands not working:**

1. **Plugin Loading:**
   ```bash
   # Check if plugin is loaded
   /velocity plugins
   ```

2. **File Permissions:**
   ```bash
   # Ensure plugin file has correct permissions
   chmod 644 SkyeNetV-2.4.1.jar
   ```

3. **Java Version:**
   - Ensure you're using Java 17 or higher
   - Velocity requires modern Java versions

4. **Dependencies:**
   - Velocity API compatibility
   - LuckPerms (optional, for prefix support)

## Configuration Issues

### Discord Integration Not Working

1. **Check discord_config.yml:**
   ```yaml
   discord:
     token: "your-bot-token-here"
     channel_id: "your-channel-id-here"
     name_format: "username"  # or "displayname"
   ```

2. **Bot Permissions:**
   - Bot needs read/write message permissions
   - Ensure bot is in the correct channel

### Global Chat Not Broadcasting

1. **Multiple Servers Setup:**
   - Global chat only works across multiple servers
   - Need at least 2 servers connected to Velocity

2. **Player Must Enable:**
   - Players must run `/gc` to enable global chat
   - Chat is per-player preference

## Installation Verification

### Quick Test Commands
```bash
# Test basic functionality
/rules                 # Should show server rules
/gc                   # Should toggle global chat
/lobby                # Should connect to lobby (if server exists)
/discord              # Should show Discord info
```

### Log Checking
```bash
# Check Velocity logs for errors
tail -f logs/latest.log | grep SkyeNetV
```

## Common Error Messages

### "You must be a player!"
- **Cause:** Using player-only command from console
- **Solution:** Run command in-game as a player

### "Error: 'lobby' server is not registered"
- **Cause:** No server named "lobby" in Velocity config
- **Solution:** Add lobby server to velocity.toml or rename in plugin

### "This command can only be used by players!"
- **Cause:** Player-specific command used from console
- **Solution:** Use command in-game

### No response to commands
- **Cause:** Plugin not loaded or command not registered
- **Solution:** Check plugin loading, restart Velocity if needed

## Getting Help

1. **Check Console Logs:** Most issues show up in Velocity console
2. **Verify Configuration:** Ensure all config files are properly formatted
3. **Test Individual Features:** Test each command separately
4. **Plugin Conflicts:** Temporarily disable other plugins to test

## Version Information

- **Current Version:** 2.4.1
- **Velocity Compatibility:** 3.0+
- **Java Requirement:** 17+
- **Optional Dependencies:** LuckPerms (for prefix support)
