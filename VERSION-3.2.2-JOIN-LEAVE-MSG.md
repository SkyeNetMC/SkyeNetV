# SkyeNetV Version 3.2.2 - Join & Leave messages

This update adds join & leave messages to the discord chat sync.

## New Features

### Discord join/leave messages

- Sent as a normal message
- Configurable in the config
- Default join message: `**[+]** {player} joined the network`
- Default leave message: `**[-]** {player} left the network`

## Technical Implementation

- Added sendJoinMessageToDiscord() and sendLeaveMessageToDiscord() functions in JoinLeaveListener.java
- Added `discord.game_to_discord_join_format` and `discord.game_to_discord_leave_format` to the config

## Configuration

The discord join/leave messages can be customized in the config.yml file:
```yaml
discord:
  game_to_discord_join_format: "**[+]** {player} joined the network"
  game_to_discord_leave_format: "**[-]** {player} left the network"
```
