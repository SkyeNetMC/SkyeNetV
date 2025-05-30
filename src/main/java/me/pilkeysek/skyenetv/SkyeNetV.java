package me.pilkeysek.skyenetv;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import me.pilkeysek.skyenetv.config.RulesConfig;
import me.pilkeysek.skyenetv.config.DiscordConfig;
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

import java.nio.file.Path;

@Plugin(id = "skyenetv", name = "SkyeNet Velocity Plugin", version = "2.2",
        url = "skye.host", description = "Utilities for SkyeNet Velocity ProxyServer (smth like that)", authors = {"PilkeySEK"})
public class SkyeNetV {

    private final ProxyServer server;
    private final Logger logger;
    private DiscordManager discordManager;
    private ChatFilterModule chatFilterModule;
    private final Path dataDirectory;
    private DiscordConfig discordConfig;
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
        discordConfig = new DiscordConfig(dataDirectory, logger);
        rulesConfig = new RulesConfig(dataDirectory, logger);

        logger.info("SkyeNetV initialized!");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("discord").plugin(this).build(), new DiscordCommand(this));
        commandManager.register(commandManager.metaBuilder("lobby").aliases("l", "hub").plugin(this).build(), new LobbyCommand(server));
        commandManager.register(commandManager.metaBuilder("sudo").plugin(this).build(), new SudoCommand(server, logger));
        commandManager.register(commandManager.metaBuilder("rules").plugin(this).build(), new RulesCommand(rulesConfig));

        // Initialize Chat Filter Module
        chatFilterModule = new ChatFilterModule(server, logger, dataDirectory);
        server.getEventManager().register(this, chatFilterModule);
        commandManager.register(commandManager.metaBuilder("chatfilter").aliases("cf").plugin(this).build(), new ChatFilterCommand(chatFilterModule));

        // Initialize Discord bot
        if (!discordConfig.isConfigured()) {
            logger.warn("Please configure your Discord bot token and channel ID in discord_config.yml");
            return;
        }

        discordManager = new DiscordManager(this, discordConfig);
        discordManager.getJda().addEventListener(new DiscordListener(this, discordManager));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (discordManager != null) {
            discordManager.shutdown();
        }
    }

    @Subscribe(order = PostOrder.LATE)
    public void onPlayerChat(PlayerChatEvent event) {
        // Handle Discord integration only for messages that passed the chat filter
        // The chat filter runs with EARLY priority, so this runs after filtering is complete
        if (discordManager != null && event.getPlayer().getCurrentServer().isPresent()) {
            // Only send to Discord if the message passes all filters and is allowed
            if (event.getResult().isAllowed()) {
                discordManager.sendChatMessage(
                    event.getPlayer(),
                    event.getMessage(),
                    event.getPlayer().getCurrentServer().get().getServer()
                );
            }
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
    
    public DiscordConfig getDiscordConfig() {
        return discordConfig;
    }
    
    public ChatFilterModule getChatFilterModule() {
        return chatFilterModule;
    }
    
    public void reloadDiscordConfig() throws Exception {
        logger.info("Reloading Discord configuration...");
        
        // Load new configuration
        DiscordConfig newConfig = new DiscordConfig(dataDirectory, logger);
        
        // Update the current configuration
        this.discordConfig = newConfig;
        
        // Restart Discord manager with new configuration
        if (discordManager != null) {
            discordManager.shutdown();
        }
        
        discordManager = new DiscordManager(this, discordConfig);
        
        logger.info("Discord configuration reloaded successfully!");
    }
}
