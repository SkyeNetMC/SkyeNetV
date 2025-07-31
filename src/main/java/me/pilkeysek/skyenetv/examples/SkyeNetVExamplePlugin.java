package me.pilkeysek.skyenetv.examples;

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

/**
 * Example plugin demonstrating SkyeNetV API usage
 * This is a reference implementation for other developers
 */
@Plugin(id = "skyenetv-example", name = "SkyeNetV API Example", version = "1.0.0")
public class SkyeNetVExamplePlugin {
    
    private final ProxyServer server;
    private final Logger logger;
    private SkyeNetVAPI skyeNetAPI;
    private final MiniMessage mm = MiniMessage.miniMessage();
    
    @Inject
    public SkyeNetVExamplePlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }
    
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Check if SkyeNetV API is available
        if (SkyeNetVProvider.isAvailable()) {
            skyeNetAPI = SkyeNetVProvider.getAPI();
            logger.info("SkyeNetV API connected! Registering example commands...");
            
            // Register example commands to demonstrate API usage
            registerCommands();
        } else {
            logger.warn("SkyeNetV API not available. Example commands will not be registered.");
        }
    }
    
    private void registerCommands() {
        var commandManager = server.getCommandManager();
        
        // Command to send cross-server alerts
        commandManager.register(
            commandManager.metaBuilder("alert").plugin(this).build(),
            new AlertCommand()
        );
        
        // Command to find players across servers
        commandManager.register(
            commandManager.metaBuilder("locate").plugin(this).build(),
            new LocateCommand()
        );
        
        // Command to get network statistics
        commandManager.register(
            commandManager.metaBuilder("networkstats").plugin(this).build(),
            new NetworkStatsCommand()
        );
    }
    
    /**
     * Example command: Send alerts to specific players across servers
     */
    public class AlertCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();
            
            if (args.length < 2) {
                source.sendMessage(mm.deserialize("<red>Usage: /alert <player> <message></red>"));
                return;
            }
            
            String targetName = args[0];
            String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            
            Component alertMessage = mm.deserialize(
                "<red><bold>[ALERT]</bold></red> <yellow>" + message + "</yellow>"
            );
            
            boolean sent = skyeNetAPI.sendCrossServerMessage(targetName, alertMessage);
            
            if (sent) {
                source.sendMessage(mm.deserialize("<green>Alert sent to " + targetName + "!</green>"));
            } else {
                source.sendMessage(mm.deserialize("<red>Player '" + targetName + "' not found!</red>"));
            }
        }
        
        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission("skyenetv.example.alert");
        }
    }
    
    /**
     * Example command: Locate players across the network
     */
    public class LocateCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();
            
            if (args.length != 1) {
                source.sendMessage(mm.deserialize("<red>Usage: /locate <player></red>"));
                return;
            }
            
            String playerName = args[0];
            Optional<Player> player = skyeNetAPI.findPlayer(playerName);
            
            if (player.isPresent()) {
                Optional<String> serverName = skyeNetAPI.getPlayerServer(player.get());
                
                if (serverName.isPresent()) {
                    source.sendMessage(mm.deserialize(
                        "<aqua>🔍 Player Found:</aqua> <green>" + player.get().getUsername() + 
                        "</green> <gray>is currently on</gray> <yellow>" + serverName.get() + "</yellow>"
                    ));
                } else {
                    source.sendMessage(mm.deserialize("<red>Could not determine server for " + player.get().getUsername() + "</red>"));
                }
            } else {
                source.sendMessage(mm.deserialize("<red>Player '" + playerName + "' not found on the network!</red>"));
            }
        }
        
        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission("skyenetv.example.locate");
        }
    }
    
    /**
     * Example command: Show network statistics
     */
    public class NetworkStatsCommand implements SimpleCommand {
        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();
            
            var allPlayers = skyeNetAPI.getAllPlayers();
            int totalPlayers = allPlayers.size();
            
            // Count players per server
            java.util.Map<String, Integer> serverCounts = new java.util.HashMap<>();
            
            for (Player player : allPlayers) {
                Optional<String> serverName = skyeNetAPI.getPlayerServer(player);
                if (serverName.isPresent()) {
                    serverCounts.merge(serverName.get(), 1, Integer::sum);
                }
            }
            
            source.sendMessage(mm.deserialize("<gold>========== Network Statistics ==========</gold>"));
            source.sendMessage(mm.deserialize("<aqua>Total Players Online:</aqua> <white>" + totalPlayers + "</white>"));
            source.sendMessage(mm.deserialize("<aqua>Servers:</aqua>"));
            
            for (var entry : serverCounts.entrySet()) {
                source.sendMessage(mm.deserialize(
                    "<gray>  • </gray><yellow>" + entry.getKey() + "</yellow><gray>: </gray><white>" + entry.getValue() + " players</white>"
                ));
            }
            
            source.sendMessage(mm.deserialize("<gold>======================================</gold>"));
        }
        
        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission("skyenetv.example.stats");
        }
    }
}
