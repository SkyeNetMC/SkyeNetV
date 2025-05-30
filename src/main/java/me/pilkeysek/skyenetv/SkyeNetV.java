package me.pilkeysek.skyenetv;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import me.pilkeysek.skyenetv.config.RulesConfig;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import me.pilkeysek.skyenetv.discord.DiscordListener;
import me.pilkeysek.skyenetv.commands.DiscordCommand;
import me.pilkeysek.skyenetv.commands.LobbyCommand;
import me.pilkeysek.skyenetv.commands.RulesCommand;
import me.pilkeysek.skyenetv.commands.SudoCommand;
import me.pilkeysek.skyenetv.commands.ChatFilterCommand;
import me.pilkeysek.skyenetv.modules.ChatFilterModule;
import org.slf4j.Logger;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

@Plugin(id = "skyenetv", name = "SkyeNet Velocity Plugin", version = "2.1",
        url = "skye.host", description = "Utilities for SkyeNet Velocity ProxyServer (smth like that)", authors = {"PilkeySEK"})
public class SkyeNetV {

    private final ProxyServer server;
    private final Logger logger;
    private DiscordManager discordManager;
    private ChatFilterModule chatFilterModule;
    private final Path dataDirectory;
    private Properties config;
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

        // Load or create config
        loadConfig();

        logger.info("SkyeNetV initialized!");
    }

    private void loadConfig() {
        config = new Properties();
        File configFile = dataDirectory.resolve("config.properties").toFile();

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                config.setProperty("discord.token", "YOUR_BOT_TOKEN_HERE");
                config.setProperty("discord.channel", "YOUR_CHANNEL_ID_HERE");
                config.store(java.nio.file.Files.newOutputStream(configFile.toPath()), "SkyeNetV Configuration");
            } catch (IOException e) {
                logger.error("Failed to create config file", e);
            }
        }

        try {
            config.load(java.nio.file.Files.newInputStream(configFile.toPath()));
        } catch (IOException e) {
            logger.error("Failed to load config file", e);
        }
        
        // Initialize rules config
        rulesConfig = new RulesConfig(dataDirectory, logger);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("discord").plugin(this).build(), new DiscordCommand());
        commandManager.register(commandManager.metaBuilder("lobby").aliases("l", "hub").plugin(this).build(), new LobbyCommand(server));
        commandManager.register(commandManager.metaBuilder("sudo").plugin(this).build(), new SudoCommand(server, logger));
        commandManager.register(commandManager.metaBuilder("rules").plugin(this).build(), new RulesCommand(rulesConfig));

        // Initialize Chat Filter Module
        chatFilterModule = new ChatFilterModule(server, logger, dataDirectory);
        server.getEventManager().register(this, chatFilterModule);
        commandManager.register(commandManager.metaBuilder("chatfilter").aliases("cf").plugin(this).build(), new ChatFilterCommand(chatFilterModule));

        // Initialize Discord bot
        String token = config.getProperty("discord.token");
        String channelId = config.getProperty("discord.channel");

        if ("YOUR_BOT_TOKEN_HERE".equals(token) || "YOUR_CHANNEL_ID_HERE".equals(channelId)) {
            logger.warn("Please configure your Discord bot token and channel ID in config.properties");
            return;
        }

        discordManager = new DiscordManager(this, token, channelId);
        discordManager.getJda().addEventListener(new DiscordListener(this, discordManager));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (discordManager != null) {
            discordManager.shutdown();
        }
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        // The chat filter will handle filtering in its own event listener
        // This just handles Discord integration for messages that pass the filter
        if (discordManager != null && event.getPlayer().getCurrentServer().isPresent() && 
            event.getResult().isAllowed()) {
            discordManager.sendChatMessage(
                event.getPlayer(),
                event.getMessage(),
                event.getPlayer().getCurrentServer().get().getServer()
            );
        }
    }

    @Subscribe
    public void onServerSwitch(ServerPostConnectEvent event) {
        if (discordManager != null) {
            discordManager.sendServerSwitch(
                event.getPlayer(),
                event.getPreviousServer(),
                event.getPlayer().getCurrentServer().get().getServer()
            );
        }
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        if (discordManager != null) {
            discordManager.sendPlayerJoin(event.getPlayer());
        }
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        if (discordManager != null) {
            discordManager.sendPlayerLeave(event.getPlayer());
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
