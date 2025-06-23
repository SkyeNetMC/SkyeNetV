package me.pilkeysek.skyenetv;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import me.pilkeysek.skyenetv.config.RulesConfig;
import me.pilkeysek.skyenetv.config.DiscordConfig;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.commands.LobbyCommand;
import me.pilkeysek.skyenetv.commands.RulesCommand;
import me.pilkeysek.skyenetv.commands.SkyeNetVCommand;
import me.pilkeysek.skyenetv.commands.SudoCommand;
import me.pilkeysek.skyenetv.commands.DiscordCommand;
import me.pilkeysek.skyenetv.commands.FlyCommand;
import me.pilkeysek.skyenetv.utils.PrefixUtils;
import me.pilkeysek.skyenetv.utils.ChatManager;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import me.pilkeysek.skyenetv.listeners.ChatListener;
import me.pilkeysek.skyenetv.listeners.JoinLeaveListener;

import org.slf4j.Logger;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;

@Plugin(id = "skyenetv", name = "SkyeNet Velocity Plugin", version = "3.2.1",
        url = "skyemc.net", description = "Core utilities for SkyeNet Velocity ProxyServer with Discord and Local Chat", authors = {"NobleSke", "PilkeySEK"})
public class SkyeNetV {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private Config config;
    private RulesConfig rulesConfig;
    private DiscordConfig discordConfig;
    private DiscordManager discordManager;
    private ChatManager chatManager;
    private ChatListener chatListener;
    private JoinLeaveListener joinLeaveListener;

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
        config = new Config(dataDirectory, logger);
        rulesConfig = new RulesConfig(dataDirectory, logger);
        discordConfig = new DiscordConfig(config, logger);
        
        // Initialize Discord manager
        discordManager = new DiscordManager(server, logger, discordConfig);
        
        // Initialize chat manager
        chatManager = new ChatManager(server, logger, discordManager, config);
        
        // Initialize chat listener
        chatListener = new ChatListener(chatManager, logger);
        
        // Initialize join/leave listener
        joinLeaveListener = new JoinLeaveListener(server, logger, chatManager, config);

        logger.info("SkyeNetV initialized!");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        
        // Register core commands
        commandManager.register(commandManager.metaBuilder("lobby").aliases("l", "hub").plugin(this).build(), new LobbyCommand(server, config));
        commandManager.register(commandManager.metaBuilder("sudo").plugin(this).build(), new SudoCommand(server, logger));
        commandManager.register(commandManager.metaBuilder("rules").plugin(this).build(), new RulesCommand(rulesConfig));
        commandManager.register(commandManager.metaBuilder("fly").plugin(this).build(), new FlyCommand(server));
        
        // Register the main plugin command with reload functionality
        commandManager.register(commandManager.metaBuilder("skyenetv").aliases("snv").plugin(this).build(), new SkyeNetVCommand(this, config, discordManager));
        
        // Register Discord command
        commandManager.register(commandManager.metaBuilder("discord").plugin(this).build(), new DiscordCommand(config));

        // Initialize LuckPerms integration
        PrefixUtils.initialize();
        
        // Register chat listener
        server.getEventManager().register(this, chatListener);
        
        // Register join/leave listener
        server.getEventManager().register(this, joinLeaveListener);
        
        // Initialize Discord bot
        discordManager.initialize().thenAccept(success -> {
            if (success) {
                logger.info("Discord integration initialized successfully!");
            } else {
                logger.warn("Discord integration failed to initialize or is disabled");
            }
        });
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("SkyeNetV shutting down...");
        
        // Shutdown Discord manager
        if (discordManager != null) {
            discordManager.shutdown();
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
    
    public DiscordManager getDiscordManager() {
        return discordManager;
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    public Config getConfig() {
        return config;
    }
}
