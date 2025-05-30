# SkyeNetV - SkyeNet Velocity Plugin

A comprehensive Velocity plugin for the SkyeNet Minecraft server network, providing server management utilities and rules system.

## Version 2.2

### New Features in 2.2
- **Chat Filter System**: Network-wide chat filtering with comprehensive word and pattern blocking
- **Existing Filter Integration**: Uses existing `filters/regex.yml` and `filters/wordlist.yml` configurations
- **Advanced Pattern Detection**: IP blocking, spam detection, caps filtering, ASCII art detection, URL filtering
- **Bypass Permissions**: Multi-level permission system for different filter types
- **Real-time Reload**: Update filters without server restart

### Features from 2.1
- **Server Rules System**: Complete rules management with JSON-based configuration
- **MiniMessage Support**: Rich text formatting with gradients and colors
- **Admin Commands**: Full CRUD operations for rule management
- **Flexible Configuration**: JSON-based rules storage with customizable headers and footers

## Installation

1. Download the latest `SkyeNetV-2.2.jar` from releases
2. Place the JAR file in your Velocity `plugins/` directory
3. Restart your Velocity proxy server
4. The plugin will create necessary configuration files in `plugins/skyenetv/`
5. Chat filter will automatically copy existing filter configurations from resources

## Features

### 🛡️ Chat Filter System
- **Network-wide Filtering**: Filter messages across all servers in your proxy network
- **Word List Blocking**: Block specific words and phrases
- **Advanced Regex Patterns**: IP detection, spam prevention, caps filtering, and custom patterns
- **Bypass Permissions**: Allow trusted players to bypass specific filters
- **Real-time Configuration**: Reload filters without server restart
- **Discord Integration**: Filtered messages are blocked before reaching Discord

### 🛡️ Server Rules System
- **Dynamic Rule Management**: Add, edit, and remove rules on-the-fly
- **Rich Text Formatting**: Full MiniMessage support for colorful text
- **Permission-Based Access**: Admin-only commands with role-based security
- **JSON Configuration**: Easy-to-edit rules storage
- **Customizable Headers/Footers**: Personalize your rules display

### 🎨 MiniMessage Formatting
The plugin supports the full MiniMessage specification for rich text formatting:
- **Colors**: `<red>`, `<blue>`, `<green>`, etc.
- **Gradients**: `<gradient:gold:yellow>Amazing Text</gradient>`
- **Decorations**: `<bold>`, `<italic>`, `<underlined>`
- **Combined Effects**: `<red><bold>Important Rule</bold></red>`

## Commands

### `/chatfilter` - Chat Filter Management

#### Admin Commands (Requires `skyenetv.chatfilter.admin` permission)
- **`/chatfilter reload`** - Reload chat filter configuration
- **`/chatfilter help`** - Show help information

### `/rules` - Server Rules Management

#### Public Commands
- **`/rules`** - Display all server rules to the player

#### Admin Commands (Requires `skyenetv.rules.admin` permission)
- **`/rules add "<title>" "<description>"`** - Add a new rule
  - Example: `/rules add "No Griefing" "Destroying other players' builds is prohibited"`
  
- **`/rules remove <id>`** - Remove a rule by its ID number
  - Example: `/rules remove 3`
  
- **`/rules edit <id> "<title>" "<description>"`** - Edit an existing rule
  - Example: `/rules edit 1 "No Hacking" "Using unauthorized modifications is strictly forbidden"`
  
- **`/rules header <text>`** - Set the header text displayed above rules
  - Example: `/rules header "<gradient:gold:yellow>SkyeNet Server Rules</gradient>"`
  
- **`/rules footer <text>`** - Set the footer text displayed below rules
  - Example: `/rules footer "<gray>Thank you for following our rules!</gray>"`
  
- **`/rules reload`** - Reload rules from the configuration file

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `skyenetv.chatfilter.admin` | Access to chat filter management commands | OP only |
| `skyenetv.chatfilter.bypass` | Bypass all chat filters | OP only |
| `skyenetv.wordlist.bypass` | Bypass wordlist filtering only | OP only |
| `skyenetv.regex.bypass` | Bypass regex pattern filtering only | OP only |
| `skyenetv.rules.admin` | Access to all rule management commands | OP only |

## Configuration

### Chat Filter Configuration

The chat filter creates three configuration files:

#### `plugins/skyenetv/chatfilter-config.yml` - Main Configuration
```yaml
enabled: true
prefix: "<dark_red>[ChatFilter]</dark_red> "
debug: false
blocked-words: []  # Global blocked words
modules:
  ChatFilter:
    enabled: true
    wordlist:
      enabled: true
    regex:
      enabled: true
```

#### `plugins/skyenetv/chatfilter/wordlist.yml` - Blocked Words
```yaml
enabled: true
bypass-permission: "skyenetv.wordlist.bypass"
blocked-message: "<prefix>Your message was filtered for containing a blocked word: <word>"
list:
  - "badword"
  - "spam"
  - "example"
```

#### `plugins/skyenetv/chatfilter/regex.yml` - Pattern Filtering
```yaml
bypass-permission: "skyenetv.chatfilter.bypass"
Enable-regex: true
blocked-message: "<prefix>Your message was filtered by pattern: <pattern>"

block-ips:
  enabled: true
  regex: "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"

block-spam-chars:
  enabled: true
  threshold: 4
  regex: "(.)\\1{3,}"

block-caps:
  enabled: true
  threshold: 60
  min-length: 6

custom-patterns:
  enabled: false
  patterns:
    test-pattern: "\\b(badword\\d+)\\b"
```

For detailed chat filter documentation, see [CHATFILTER-README.md](CHATFILTER-README.md).

### Rules Configuration (`plugins/skyenetv/rules.json`)

The rules are stored in JSON format with the following structure:

```json
{
  "header": "<gradient:gold:yellow>SkyeNet Server Rules</gradient>",
  "footer": "<gray>Thank you for following our rules!</gray>",
  "rules": [
    {
      "id": 1,
      "title": "<red>No Hacking",
      "description": "Using unauthorized modifications or exploits is strictly prohibited."
    },
    {
      "id": 2,
      "title": "<yellow>Be Respectful",
      "description": "Treat all players with respect. No harassment, bullying, or discrimination."
    }
  ]
}
```

### Default Rules

The plugin comes with example rules that demonstrate MiniMessage formatting:
1. **No Hacking** - Prohibition of unauthorized modifications
2. **Be Respectful** - Guidelines for player interaction
3. **No Griefing** - Protection of player builds
4. **Follow Chat Rules** - Communication guidelines
5. **No Advertising** - Server promotion restrictions

## MiniMessage Examples

Here are some formatting examples you can use in your rules:

### Basic Colors
```
<red>Red text</red>
<blue>Blue text</blue>
<green>Green text</green>
<yellow>Yellow text</yellow>
<gray>Gray text</gray>
```

### Gradients
```
<gradient:red:blue>Gradient from red to blue</gradient>
<gradient:gold:yellow>Golden gradient</gradient>
<gradient:#ff0000:#00ff00>RGB gradient</gradient>
```

### Text Decorations
```
<bold>Bold text</bold>
<italic>Italic text</italic>
<underlined>Underlined text</underlined>
<strikethrough>Strikethrough text</strikethrough>
```

### Combined Effects
```
<red><bold>Important Warning!</bold></red>
<gradient:gold:yellow><italic>Special Announcement</italic></gradient>
```

## Development

### Building from Source

1. Clone the repository
2. Ensure you have Java 17+ and Maven installed
3. Run `mvn clean package`
4. The compiled JAR will be in the `target/` directory

### Project Structure
```
src/main/java/me/pilkeysek/skyenetv/
├── SkyeNetV.java                 # Main plugin class
├── commands/
│   └── RulesCommand.java         # Rules command handler
└── config/
    └── RulesConfig.java          # Rules configuration manager
```

## Support

- **GitHub Issues**: Report bugs and request features
- **Discord**: Join our community server
- **Website**: [https://skyenet.co.in](https://skyenet.co.in)

## License

This project is licensed under the MIT License.

## Authors

- **NobleSKye** - Project Lead
- **PilkeySEK** - Core Developer  
- **SkyeNetwork Team** - Contributors

---

*SkyeNetV v2.1 - Enhancing your Velocity server experience*

