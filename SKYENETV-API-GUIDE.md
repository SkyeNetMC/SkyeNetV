# SkyeNetV API Usage Guide

## Overview
The SkyeNetV API allows other Velocity plugins to integrate with SkyeNetV's cross-server functionality. This includes finding players across servers, sending cross-server messages, and broadcasting to the network.

## Getting Started

### 1. Add SkyeNetV as a Dependency

Add SkyeNetV to your plugin's dependencies in `velocity-plugin.json`:

```json
{
  "id": "your-plugin-id",
  "name": "Your Plugin Name",
  "version": "1.0.0",
  "main": "com.example.yourplugin.YourPlugin",
  "dependencies": [
    {
      "id": "skyenetv",
      "optional": false
    }
  ]
}
```

### 2. Check if SkyeNetV API is Available

Always check if the API is available before using it:

```java
import me.pilkeysek.skyenetv.api.SkyeNetVProvider;
import me.pilkeysek.skyenetv.api.SkyeNetVAPI;

public class YourPlugin {
    
    private SkyeNetVAPI skyeNetAPI;
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (SkyeNetVProvider.isAvailable()) {
            skyeNetAPI = SkyeNetVProvider.getAPI();
            logger.info("SkyeNetV API connected successfully!");
        } else {
            logger.warn("SkyeNetV API not available!");
        }
    }
}
```

## API Methods

### Finding Players

```java
// Find player by exact or partial name
Optional<Player> player = skyeNetAPI.findPlayer("PlayerName");
if (player.isPresent()) {
    // Player found
}

// Check if player is online anywhere in the network
boolean isOnline = skyeNetAPI.isPlayerOnline("PlayerName");

// Get all players across all servers
Collection<Player> allPlayers = skyeNetAPI.getAllPlayers();

// Get which server a player is on
Optional<String> serverName = skyeNetAPI.getPlayerServer(player.get());
```

### Sending Messages

```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

MiniMessage mm = MiniMessage.miniMessage();

// Send message to specific player
Component message = mm.deserialize("<green>Hello from your plugin!</green>");
boolean sent = skyeNetAPI.sendCrossServerMessage("PlayerName", message);

// Send message to player object
Player player = // ... get player
skyeNetAPI.sendCrossServerMessage(player, message);
```

### Broadcasting

```java
// Broadcast to entire network
Component announcement = mm.deserialize("<gold>Server announcement!</gold>");
skyeNetAPI.broadcastToNetwork(announcement);

// Broadcast to specific server
skyeNetAPI.broadcastToServer("survival", announcement);
```

## Example Plugin Integration

Here's a complete example of a plugin that uses the SkyeNetV API:

```java
package com.example.crossserverchat;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyenetv.api.SkyeNetVAPI;
import me.pilkeysek.skyenetv.api.SkyeNetVProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.Optional;

@Plugin(id = "crossserverchat", name = "Cross Server Chat", version = "1.0.0")
public class CrossServerChatPlugin {
    
    private final ProxyServer server;
    private final Logger logger;
    private SkyeNetVAPI skyeNetAPI;
    private final MiniMessage mm = MiniMessage.miniMessage();
    
    @Inject
    public CrossServerChatPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Check if SkyeNetV API is available
        if (SkyeNetVProvider.isAvailable()) {
            skyeNetAPI = SkyeNetVProvider.getAPI();
            logger.info("Connected to SkyeNetV API!");
            
            // Register commands
            server.getCommandManager().register(
                server.getCommandManager().metaBuilder("announce").build(),
                new AnnounceCommand()
            );
            
            server.getCommandManager().register(
                server.getCommandManager().metaBuilder("findplayer").build(),
                new FindPlayerCommand()
            );
        } else {
            logger.error("SkyeNetV API not available! This plugin requires SkyeNetV.");
        }
    }
    
    // Command to announce to all servers
    public class AnnounceCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();
            
            if (args.length == 0) {
                source.sendMessage(mm.deserialize("<red>Usage: /announce <message></red>"));
                return;
            }
            
            String message = String.join(" ", args);
            Component announcement = mm.deserialize(
                "<gold>[Network Announcement]</gold> <white>" + message + "</white>"
            );
            
            skyeNetAPI.broadcastToNetwork(announcement);
            source.sendMessage(mm.deserialize("<green>Announcement sent to all servers!</green>"));
        }
        
        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission("crossserverchat.announce");
        }
    }
    
    // Command to find which server a player is on
    public class FindPlayerCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();
            
            if (args.length != 1) {
                source.sendMessage(mm.deserialize("<red>Usage: /findplayer <name></red>"));
                return;
            }
            
            String playerName = args[0];
            Optional<Player> player = skyeNetAPI.findPlayer(playerName);
            
            if (player.isPresent()) {
                Optional<String> serverName = skyeNetAPI.getPlayerServer(player.get());
                if (serverName.isPresent()) {
                    source.sendMessage(mm.deserialize(
                        "<green>" + player.get().getUsername() + "</green> <gray>is on server:</gray> <yellow>" + serverName.get() + "</yellow>"
                    ));
                } else {
                    source.sendMessage(mm.deserialize("<red>Could not determine server for " + player.get().getUsername() + "</red>"));
                }
            } else {
                source.sendMessage(mm.deserialize("<red>Player '" + playerName + "' not found!</red>"));
            }
        }
        
        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission("crossserverchat.findplayer");
        }
    }
}
```

## Best Practices

1. **Always check availability**: Always check `SkyeNetVProvider.isAvailable()` before using the API
2. **Handle optionals**: Many methods return `Optional<>` - always check if the value is present
3. **Error handling**: Wrap API calls in try-catch blocks for production use
4. **Performance**: The API uses the same player lookup methods as SkyeNetV's private messaging system, so it's already optimized
5. **MiniMessage**: Use MiniMessage for formatting - it's consistent with SkyeNetV's approach

## Available Permissions

The API doesn't enforce permissions - that's up to your plugin. Consider using:
- `yourplugin.crossserver` - Basic cross-server functionality
- `yourplugin.broadcast` - Broadcasting to network
- `yourplugin.admin` - Administrative features

## Troubleshooting

### API is null or unavailable
- Ensure SkyeNetV is installed and enabled
- Check that your plugin loads after SkyeNetV (use dependencies in velocity-plugin.json)
- Verify SkyeNetV is the correct version

### Players not found
- Remember the API uses the same lookup as `/msg` - it supports partial names
- Check if the player is actually online with `/velocity players`

### Messages not being delivered
- Ensure the target player is still online
- Check for any exceptions in the console
- Verify the Component is properly formatted
