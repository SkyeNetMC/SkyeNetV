# Changelog

## Version 2.3 (June 5, 2025)

### Breaking Changes
- **Removed Chat Filter System**: Complete removal of chat filtering functionality
  - Deleted `ChatFilterCommand.java` and `ChatFilterModule.java`
  - Removed all filter configuration files and directories
  - Removed filter-related documentation
  - Simplified Discord integration to remove filter dependencies

### Changes
- Simplified main plugin class by removing chat filter initialization
- Updated Discord configuration to remove filter-related settings
- Streamlined event handling without filter checks
- Removed unused LuckPerms imports and dependencies
- Updated documentation to focus on rules system and Discord integration
- Preserved SnakeYAML dependency for Discord configuration

### Current Features
- **Discord Integration**: Full chat bridging with MiniMessage support
- **Rules System**: Comprehensive server rules management
- **Lobby Commands**: `/lobby`, `/l`, `/hub` teleportation
- **Administrative Tools**: Configuration reload capabilities

## Version 2.2 (Previous)
- Discord Integration enhancements
- Rules System implementation
- Chat Filter functionality (now removed)
- Server management improvements

## Version 2.1 (Previous)
- Initial Rules System
- MiniMessage support
- Admin commands
- JSON-based configuration
