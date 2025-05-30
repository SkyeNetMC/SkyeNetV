# SkyeNetV 2.2 Deployment Guide

## Quick Deployment Steps

### 1. Pre-Deployment
- **Backup** your current plugin and configurations
- Stop your Velocity proxy server
- Note current plugin version for rollback if needed

### 2. Deploy New Plugin
```bash
# Replace the old plugin
mv /path/to/velocity/plugins/SkyeNetV-2.1.jar /path/to/velocity/plugins/SkyeNetV-2.1.jar.backup
cp /mnt/sda4/SkyeNetwork/SkyeNetV/target/SkyeNetV-2.2.jar /path/to/velocity/plugins/
```

### 3. Start Server
- Start your Velocity proxy server
- Monitor console for any errors during startup
- Chat filter will automatically initialize with existing configurations

### 4. Verify Installation
```bash
# Check plugin loaded successfully
/plugins
# Should show SkyeNetV version 2.2

# Test chat filter admin commands
/chatfilter help
/chatfilter reload
```

## Chat Filter Configuration

### Automatic Setup
The chat filter will automatically:
1. Create `plugins/skyenetv/filters/` directory
2. Copy `regex.yml` and `wordlist.yml` from plugin resources
3. Load 785+ blocked words from existing wordlist
4. Enable comprehensive regex patterns (IP, spam, caps, URLs, etc.)

### Manual Configuration
If you want to customize the filters:
1. Edit `plugins/skyenetv/filters/regex.yml` for pattern settings
2. Edit `plugins/skyenetv/filters/wordlist.yml` for word blocking
3. Run `/chatfilter reload` to apply changes

## Permissions Setup

Add these permissions to your permission manager:

### Admin Permissions
- `skyenetv.chatfilter.admin` - Access to filter management commands
- `skyenetv.rules.admin` - Access to rules management commands  
- `skyenetv.sudo` - Access to sudo command

### Bypass Permissions (for trusted players)
- `skyenetv.chatfilter.bypass` - Bypass all chat filters
- `skyenetv.wordlist.bypass` - Bypass only wordlist filtering
- `skyenetv.regex.bypass` - Bypass only regex pattern filtering

## Testing Chat Filter

### Test Blocked Words
Try sending messages with common blocked words to verify filtering works.

### Test Regex Patterns
- **IP Addresses**: `192.168.1.1`
- **Spam Characters**: `helloooooo`
- **Excessive Caps**: `HELLO WORLD THIS IS VERY LOUD`
- **URLs**: `https://malicious-site.com`

### Test Bypass Permissions
Grant bypass permissions to test that trusted players can send filtered content.

## Network-Wide Filtering

The chat filter now works at the **Velocity proxy level**, meaning:
- ✅ Messages are filtered before reaching backend servers
- ✅ Consistent filtering across all servers in your network
- ✅ Filtered messages don't reach Discord integration
- ✅ Reduces load on individual Minecraft servers

## Rollback Instructions

If you need to rollback:
```bash
# Stop Velocity server
# Restore backup
mv /path/to/velocity/plugins/SkyeNetV-2.1.jar.backup /path/to/velocity/plugins/SkyeNetV-2.1.jar
rm /path/to/velocity/plugins/SkyeNetV-2.2.jar
# Remove chat filter configs if desired
rm -rf /path/to/velocity/plugins/skyenetv/filters/
# Start server
```

## Support

- Check console logs for any error messages
- Verify file permissions on plugin directory
- Ensure Velocity version compatibility (3.1.1+)
- Review `CHATFILTER-README.md` for detailed configuration options

---

**Version**: SkyeNetV 2.2  
**Build Date**: May 30, 2025  
**Size**: 40KB  
**Dependencies**: SnakeYAML 2.0, Adventure Text 4.14.0, JDA 5.0.0-beta.13
