# SkyeNetV Version 3.2.1 - Administration Improvements

This update adds a plugin-wide reload command and improves the administration experience for server operators.

## New Features

### `/skyenetv reload` Command
- Added a central plugin reload command
- Can be used with `/skyenetv reload` or the shorter alias `/snv reload`
- Requires `skyenetv.admin` permission
- Reloads all plugin settings from the configuration file
- Reinitializes the Discord connection with the new settings

### Improved Configuration Handling
- Added configurable success/failure messages for reload operations
- Added version number display in the plugin info command
- Updated configuration version number to match plugin version

### Discord Integration Updates
- Removed the separate `/discord reload` command to consolidate administration
- Maintained all Discord functionality with the main reload command
- Simplified discord-related configuration options

## Technical Implementation

- Added `reload()` method to the Config class
- Created a new SkyeNetVCommand class for plugin administration
- Updated command registration in the main SkyeNetV class
- Added appropriate permission checks for administrative functions
- Updated Discord connection handling to support runtime reconfiguration

## Usage Examples

### View Plugin Information
```
/skyenetv
```

### Reload Plugin Configuration
```
/skyenetv reload
```
or
```
/snv reload
```

## Permission Nodes

- `skyenetv.admin` - Access to administrative commands (reload)
- `skyenetv.discord` - Access to Discord commands

## Configuration

The reload success and failure messages can be customized in the config.yml file:
```yaml
messages:
  reload_success: "<green>✅ SkyeNetV configuration reloaded successfully!</green>"
  reload_failure: "<red>❌ Failed to reload configuration. Check console for details.</red>"
```
