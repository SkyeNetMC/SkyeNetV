package me.pilkeysek.skyenetv;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.config.RulesConfig;
import me.pilkeysek.skyenetv.commands.LobbyCommand;
import me.pilkeysek.skyenetv.commands.RulesCommand;
import me.pilkeysek.skyenetv.commands.SudoCommand;
import me.pilkeysek.skyenetv.utils.PrefixUtils;

import org.slf4j.Logger;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;

@Plugin(id = "skyenetv", name = "SkyeNet Velocity Plugin", version = "3.0.0",
        url = "skyemc.net", description = "Core utilities for SkyeNet Velocity ProxyServer", authors = {"NobleSke", "PilkeySEK"})
public class SkyeNetV {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private RulesConfig rulesConfig;

    @Inject
    public SkyeNetV(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        // Create config directory if it doesn't exist
        if (!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdirs();
        }

        // Initialize configurations
        rulesConfig = new RulesConfig(dataDirectory, logger);

        logger.info("SkyeNetV initialized!");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        
        commandManager.register(commandManager.metaBuilder("lobby").aliases("l", "hub").plugin(this).build(), new LobbyCommand(server));
        commandManager.register(commandManager.metaBuilder("sudo").plugin(this).build(), new SudoCommand(server, logger));
        commandManager.register(commandManager.metaBuilder("rules").plugin(this).build(), new RulesCommand(rulesConfig));

        // Initialize LuckPerms integration
        PrefixUtils.initialize();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("SkyeNetV shutting down...");
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        // Disable vanilla join message and send custom one
        event.getPlayer().sendMessage(PrefixUtils.createJoinMessage(event.getPlayer()));
        
        // Broadcast custom join message to all players
        for (com.velocitypowered.api.proxy.Player player : server.getAllPlayers()) {
            if (!player.equals(event.getPlayer())) {
                player.sendMessage(PrefixUtils.createJoinMessage(event.getPlayer()));
            }
        }
    }

    @Subscribe  
    public void onPlayerLeave(DisconnectEvent event) {
        // Broadcast custom leave message to all remaining players
        for (com.velocitypowered.api.proxy.Player player : server.getAllPlayers()) {
            if (!player.equals(event.getPlayer())) {
                player.sendMessage(PrefixUtils.createLeaveMessage(event.getPlayer()));
            }
        }
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }
    
    public RulesConfig getRulesConfig() {
        return rulesConfig;
    }
}
