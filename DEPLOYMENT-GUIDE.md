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

### 4. Verify Installation
```bash
# Check plugin loaded successfully
/plugins
# Should show SkyeNetV version 2.2

# Test core commands
/rules
/lobby
```

## Configuration

### Discord Integration
Configure your Discord bot token and channel ID in `discord_config.yml`.

### Rules Configuration
The plugin will create a default `rules.json` file with example rules.

## Permissions Setup

Add these permissions to your permission manager:

### Admin Permissions
- `skyenetv.rules.admin` - Access to rules management commands  
- `skyenetv.sudo` - Access to sudo command

## Testing Features

### Test Rules System
- Use `/rules` to display rules
- Use `/rules add "Title" "Description"` to add rules (with admin permission)

### Test Discord Integration
- Send messages in-game to verify Discord bridge functionality

## Rollback Instructions

If you need to rollback:
```bash
# Stop Velocity server
# Restore backup
mv /path/to/velocity/plugins/SkyeNetV-2.1.jar.backup /path/to/velocity/plugins/SkyeNetV-2.1.jar
rm /path/to/velocity/plugins/SkyeNetV-2.2.jar
# Start server
```

## Support

- Check console logs for any error messages
- Verify file permissions on plugin directory
- Ensure Velocity version compatibility (3.1.1+)
- Review plugin documentation for configuration options

---

**Version**: SkyeNetV 2.2  
**Build Date**: June 5, 2025  
**Dependencies**: SnakeYAML 2.0, Adventure Text 4.14.0, JDA 5.0.0-beta.13
