# Chat Filter Module for SkyeNetV

The Chat Filter module provides comprehensive chat filtering capabilities for your Velocity proxy server, filtering messages across all connected servers in your network.

## Features

- **Word List Filtering**: Block specific words or phrases
- **Regex Pattern Filtering**: Advanced pattern matching including:
  - IP address detection and blocking
  - Spam character detection (repeated characters)
  - Excessive caps detection
  - Custom regex patterns
- **Bypass Permissions**: Allow trusted players to bypass filters
- **Configurable Messages**: Customize filter notification messages
- **Real-time Configuration**: Reload configurations without restarting

## Commands

- `/chatfilter reload` - Reload the chat filter configuration
- `/chatfilter help` - Show help information

## Permissions

- `skyenetv.chatfilter.admin` - Access to chat filter commands
- `skyenetv.chatfilter.bypass` - Bypass all chat filters
- `skyenetv.wordlist.bypass` - Bypass wordlist filtering only
- `skyenetv.regex.bypass` - Bypass regex filtering only

## Configuration Files

The chat filter creates three configuration files in your plugin's data directory:

### `chatfilter-config.yml`
Main configuration file:
```yaml
enabled: true
prefix: "<dark_red>[ChatFilter]</dark_red> "
replacement: "<red>[Filtered]</red>"
debug: false
blocked-words: []
modules:
  ChatFilter:
    enabled: true
    wordlist:
      enabled: true
    regex:
      enabled: true
```

### `chatfilter/wordlist.yml`
Word list configuration:
```yaml
enabled: true
bypass-permission: "skyenetv.wordlist.bypass"
blocked-message: "<prefix>Your message was filtered for containing a blocked word: <word>"
list:
  - "badword"
  - "example"
  - "test"
```

### `chatfilter/regex.yml`
Regex patterns configuration:
```yaml
bypass-permission: "skyenetv.chatfilter.bypass"
Enable-regex: true
blocked-message: "<prefix>Your message was filtered by pattern: <pattern>"

block-ips:
  enabled: true
  regex: "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"
  advanced-regex: "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"

block-spam-chars:
  enabled: true
  threshold: 4
  regex: "(.)\\1{3,}"
  whitelist:
    - "...."
    - "????"

block-caps:
  enabled: true
  threshold: 60
  min-length: 6

custom-patterns:
  enabled: false
  patterns:
    test-pattern: "\\b(badword\\d+)\\b"
```

## How It Works

1. **Event Handling**: The chat filter listens to `PlayerChatEvent` from Velocity
2. **Permission Checking**: Checks if players have bypass permissions
3. **Word List Filtering**: Scans messages against blocked words list
4. **Regex Filtering**: Applies regex patterns for advanced filtering
5. **Message Blocking**: Blocks filtered messages and notifies players
6. **Network-wide**: Works across all servers connected to your Velocity proxy

## Message Filtering Priority

1. Wordlist filtering (if enabled and no bypass permission)
2. IP address filtering
3. Spam character filtering
4. Excessive caps filtering
5. Custom regex patterns

## Examples

### Adding Blocked Words
Edit `chatfilter/wordlist.yml`:
```yaml
list:
  - "spam"
  - "advertisement"
  - "badword"
```

### Custom Regex Patterns
Edit `chatfilter/regex.yml`:
```yaml
custom-patterns:
  enabled: true
  patterns:
    urls: "https?://[\\w\\.-]+\\.[a-zA-Z]{2,}"
    emails: "[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}"
```

### Bypass Permissions
Grant bypass permissions in your permissions plugin:
```
- skyenetv.chatfilter.bypass  # Bypass all filters
- skyenetv.wordlist.bypass   # Bypass only wordlist
- skyenetv.regex.bypass      # Bypass only regex patterns
```

## Integration with Discord

Messages that pass the chat filter will be forwarded to Discord (if Discord integration is enabled). Filtered messages are blocked before reaching Discord.

## Troubleshooting

1. **Filters not working**: Check that the module is enabled in `chatfilter-config.yml`
2. **Permissions issues**: Verify permission plugin configuration
3. **Regex errors**: Check server logs for regex compilation errors
4. **Debug mode**: Enable debug mode to see detailed filtering logs

## Support

For support and updates, visit: https://github.com/your-repo/SkyeNetV
