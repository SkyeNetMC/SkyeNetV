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
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.pilkeysek.skyenetv.config.RulesConfig;
import me.pilkeysek.skyenetv.config.DiscordConfig;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import me.pilkeysek.skyenetv.commands.DiscordCommand;
import me.pilkeysek.skyenetv.commands.LobbyCommand;
import me.pilkeysek.skyenetv.commands.LocalChatCommand;
import me.pilkeysek.skyenetv.commands.RulesCommand;
import me.pilkeysek.skyenetv.commands.SudoCommand;
import me.pilkeysek.skyenetv.commands.GlobalChatCommand;
import me.pilkeysek.skyenetv.utils.PrefixUtils;

import org.slf4j.Logger;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Path;

@Plugin(id = "skyenetv", name = "SkyeNet Velocity Plugin", version = "2.4.3",
        url = "skye.host", description = "Utilities for SkyeNet Velocity ProxyServer (smth like that)", authors = {"PilkeySEK"})
public class SkyeNetV {

    private final ProxyServer server;
    private final Logger logger;
    private DiscordManager discordManager;
    private GlobalChatCommand globalChatCommand;
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
        globalChatCommand = new GlobalChatCommand(this);
        
        commandManager.register(commandManager.metaBuilder("discord").plugin(this).build(), new DiscordCommand(this));
        commandManager.register(commandManager.metaBuilder("lobby").aliases("l", "hub").plugin(this).build(), new LobbyCommand(server));
        commandManager.register(commandManager.metaBuilder("sudo").plugin(this).build(), new SudoCommand(server, logger));
        commandManager.register(commandManager.metaBuilder("rules").plugin(this).build(), new RulesCommand(rulesConfig));
        commandManager.register(commandManager.metaBuilder("gc").aliases("globalchat").plugin(this).build(), globalChatCommand);
        commandManager.register(commandManager.metaBuilder("lc").aliases("localchat").plugin(this).build(), new LocalChatCommand(this));

        // Initialize LuckPerms integration
        PrefixUtils.initialize();

        // Initialize Discord bot
        if (!discordConfig.isConfigured()) {
            logger.warn("Please configure your Discord bot token and channel ID in config.yml");
            return;
        }

        discordManager = new DiscordManager(this, discordConfig);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (discordManager != null) {
            discordManager.shutdown();
        }
    }

    @Subscribe(order = PostOrder.LATE)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Handle global chat broadcasting
        if (player.getCurrentServer().isPresent() && globalChatCommand.shouldSendGlobalMessages(player)) {
            String serverName = player.getCurrentServer().get().getServerInfo().getName();
            
            // Create global chat message with enhanced formatting
            Component globalChatMessage;
            
            // Add globe icon if player has it enabled
            if (globalChatCommand.shouldShowIcon(player)) {
                Component globeIcon = Component.text("🌐 ", NamedTextColor.GOLD)
                        .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                                Component.text()
                                        .append(Component.text("Global Chat", NamedTextColor.GOLD, net.kyori.adventure.text.format.TextDecoration.BOLD))
                                        .append(Component.newline())
                                        .append(Component.text("Server: ", NamedTextColor.GRAY))
                                        .append(Component.text(serverName, NamedTextColor.YELLOW))
                                        .append(Component.newline())
                                        .append(Component.text("Player: ", NamedTextColor.GRAY))
                                        .append(Component.text(player.getUsername(), NamedTextColor.WHITE))
                                        .build()
                        ));
                
                // Add formatted player name with LuckPerms colors
                Component formattedName = PrefixUtils.getFullFormattedName(player);
                
                globalChatMessage = Component.text()
                        .append(globeIcon)
                        .append(formattedName)
                        .append(Component.text(": ", NamedTextColor.WHITE))
                        .append(Component.text(message, NamedTextColor.WHITE))
                        .build();
            } else {
                // No globe icon version
                Component formattedName = PrefixUtils.getFullFormattedName(player);
                
                globalChatMessage = Component.text()
                        .append(formattedName)
                        .append(Component.text(": ", NamedTextColor.WHITE))
                        .append(Component.text(message, NamedTextColor.WHITE))
                        .build();
            }
            
            // Send to all players who should receive global messages (including sender)
            for (Player onlinePlayer : server.getAllPlayers()) {
                if (globalChatCommand.shouldReceiveGlobalMessages(onlinePlayer)) {
                    onlinePlayer.sendMessage(globalChatMessage);
                }
            }
            
            // Cancel the original event to prevent duplication
            event.setResult(PlayerChatEvent.ChatResult.denied());
        }
        
        // Handle Discord integration with new logic
        if (discordManager != null && event.getPlayer().getCurrentServer().isPresent()) {
            RegisteredServer currentServer = event.getPlayer().getCurrentServer().get().getServer();
            String serverName = currentServer.getServerInfo().getName();
            
            // Check if server is in disabled list
            if (discordConfig.getDisabledServers().contains(serverName)) {
                logger.debug("Server {} is in disabled list, not sending to Discord", serverName);
                return; // Don't send to Discord for disabled servers
            }
            
            // Check if only global chat should go to Discord
            if (discordConfig.isOnlyGlobalChatToDiscord()) {
                // Only send to Discord if player has global chat enabled and is sending messages
                if (globalChatCommand.shouldSendGlobalMessages(player)) {
                    discordManager.sendChatMessage(player, message, currentServer);
                    logger.debug("Sent global chat message to Discord from {}", player.getUsername());
                } else {
                    logger.debug("Player {} doesn't have global chat enabled, not sending to Discord", player.getUsername());
                }
            } else {
                // Send all chat to Discord (original behavior)
                discordManager.sendChatMessage(player, message, currentServer);
                logger.debug("Sent regular chat message to Discord from {}", player.getUsername());
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
        Player player = event.getPlayer();
        
        if (discordManager != null) {
            discordManager.sendPlayerJoin(player);
        }
        
        // Send global chat notification if global chat is disabled
        if (globalChatCommand != null && !globalChatCommand.isGlobalChatEnabled(player)) {
            // Delay the notification slightly so it appears after join messages
            server.getScheduler().buildTask(this, () -> {
                globalChatCommand.sendGlobalChatDisabledNotification(player);
            }).delay(1, java.util.concurrent.TimeUnit.SECONDS).schedule();
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
    
    public GlobalChatCommand getGlobalChatCommand() {
        return globalChatCommand;
    }
    
    public DiscordManager getDiscordManager() {
        return discordManager;
    }
    
    public void reloadDiscordConfig() throws Exception {
        logger.info("Reloading configuration...");
        
        // Load new configuration
        DiscordConfig newConfig = new DiscordConfig(dataDirectory, logger);
        
        // Update the current configuration
        this.discordConfig = newConfig;
        
        // Restart Discord manager with new configuration
        if (discordManager != null) {
            discordManager.shutdown();
        }
        
        discordManager = new DiscordManager(this, discordConfig);
        
        logger.info("Configuration reloaded successfully!");
    }
}
