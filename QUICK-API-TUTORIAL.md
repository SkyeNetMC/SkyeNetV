# Quick SkyeNetV API Tutorial

## Step-by-Step: Using SkyeNetV API in Your Plugin

### Step 1: Create Your Plugin Structure

Create a new plugin project with this structure:
```
your-plugin/
├── src/main/java/com/yourname/yourplugin/
│   └── YourPlugin.java
├── src/main/resources/
│   └── velocity-plugin.json
└── pom.xml
```

### Step 2: Set Up Dependencies

**velocity-plugin.json:**
```json
{
  "id": "your-plugin",
  "name": "Your Plugin",
  "version": "1.0.0",
  "main": "com.yourname.yourplugin.YourPlugin",
  "dependencies": [
    {
      "id": "skyenetv",
      "optional": false
    }
  ]
}
```

**pom.xml dependencies:**
```xml
<dependency>
    <groupId>com.velocitypowered</groupId>
    <artifactId>velocity-api</artifactId>
    <version>3.1.1</version>
    <scope>provided</scope>
</dependency>
```

### Step 3: Basic Plugin Template

**YourPlugin.java:**
```java
package com.yourname.yourplugin;

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

@Plugin(id = "your-plugin", name = "Your Plugin", version = "1.0.0")
public class YourPlugin {
    
    private final ProxyServer server;
    private final Logger logger;
    private SkyeNetVAPI api;
    private final MiniMessage mm = MiniMessage.miniMessage();
    
    @Inject
    public YourPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Step 1: Check if SkyeNetV API is available
        if (SkyeNetVProvider.isAvailable()) {
            api = SkyeNetVProvider.getAPI();
            logger.info("✅ Connected to SkyeNetV API!");
            
            // Step 2: Register your commands
            registerCommands();
        } else {
            logger.error("❌ SkyeNetV API not available! Make sure SkyeNetV is installed.");
        }
    }
    
    private void registerCommands() {
        var commandManager = server.getCommandManager();
        
        // Register a simple broadcast command
        commandManager.register(
            commandManager.metaBuilder("broadcast").build(),
            new BroadcastCommand()
        );
        
        // Register a player finder command
        commandManager.register(
            commandManager.metaBuilder("findplayer").build(),
            new FindPlayerCommand()
        );
    }
    
    // Example 1: Simple broadcast command
    public class BroadcastCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();
            
            if (args.length == 0) {
                source.sendMessage(mm.deserialize("<red>Usage: /broadcast <message></red>"));
                return;
            }
            
            // Join all arguments into a message
            String message = String.join(" ", args);
            
            // Create a formatted component
            Component broadcastMessage = mm.deserialize(
                "<gold>[BROADCAST]</gold> <white>" + message + "</white>"
            );
            
            // Use SkyeNetV API to broadcast to all servers
            api.broadcastToNetwork(broadcastMessage);
            
            // Confirm to the sender
            source.sendMessage(mm.deserialize("<green>Broadcast sent to all servers!</green>"));
        }
    }
    
    // Example 2: Find player across servers
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
            
            // Use SkyeNetV API to find player across all servers
            Optional<Player> player = api.findPlayer(playerName);
            
            if (player.isPresent()) {
                // Get which server they're on
                Optional<String> serverName = api.getPlayerServer(player.get());
                
                if (serverName.isPresent()) {
                    source.sendMessage(mm.deserialize(
                        "<green>Found:</green> <yellow>" + player.get().getUsername() + 
                        "</yellow> <gray>on server</gray> <aqua>" + serverName.get() + "</aqua>"
                    ));
                } else {
                    source.sendMessage(mm.deserialize("<red>Found player but couldn't determine server</red>"));
                }
            } else {
                source.sendMessage(mm.deserialize("<red>Player '" + playerName + "' not found online</red>"));
            }
        }
    }
}
```

## Common Use Cases

### 1. Send Alert to Specific Player
```java
public void sendAlert(String playerName, String alertMessage) {
    if (api != null) {
        Component alert = mm.deserialize("<red><bold>[ALERT]</bold></red> " + alertMessage);
        boolean sent = api.sendCrossServerMessage(playerName, alert);
        
        if (sent) {
            logger.info("Alert sent to " + playerName);
        } else {
            logger.warn("Player " + playerName + " not found");
        }
    }
}
```

### 2. Broadcast to Specific Server
```java
public void announceToServer(String serverName, String message) {
    if (api != null) {
        Component announcement = mm.deserialize("<gold>[" + serverName.toUpperCase() + "]</gold> " + message);
        api.broadcastToServer(serverName, announcement);
    }
}
```

### 3. Get Network Statistics
```java
public void showNetworkStats(CommandSource source) {
    if (api != null) {
        var allPlayers = api.getAllPlayers();
        int totalPlayers = allPlayers.size();
        
        source.sendMessage(mm.deserialize("<aqua>Network has <white>" + totalPlayers + "</white> players online</aqua>"));
        
        // Count players per server
        java.util.Map<String, Integer> serverCounts = new java.util.HashMap<>();
        for (Player player : allPlayers) {
            api.getPlayerServer(player).ifPresent(server -> 
                serverCounts.merge(server, 1, Integer::sum)
            );
        }
        
        serverCounts.forEach((server, count) -> 
            source.sendMessage(mm.deserialize("<gray>• <yellow>" + server + "</yellow>: <white>" + count + "</white> players"))
        );
    }
}
```

## Important Notes

1. **Always check if API is available** with `SkyeNetVProvider.isAvailable()`
2. **Handle Optional returns** - many methods return `Optional<>` values
3. **Use MiniMessage** for consistent formatting with SkyeNetV
4. **Add proper dependencies** in your velocity-plugin.json
5. **Test with multiple servers** to verify cross-server functionality works

## Testing Your Plugin

1. Install SkyeNetV on your Velocity proxy
2. Install your plugin 
3. Connect to different backend servers
4. Test commands work across servers
5. Check console for any errors

That's it! You now have a working plugin that integrates with SkyeNetV's cross-server functionality.
