package me.pilkeysek.skyenetv.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import me.pilkeysek.skyenetv.utils.ChatManager;
import me.pilkeysek.skyenetv.config.MessageType;
import me.pilkeysek.skyenetv.config.ServerGroupsRegistry;
import me.pilkeysek.skyenetv.utils.PrefixUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

public class JoinLeaveListener {
    
    private final ProxyServer server;
    private final Logger logger;
    private final ChatManager chatManager;
    private final DiscordManager discordManager;
    private final Config config;
    private final ServerGroupsRegistry serverGroupsRegistry;
    private final MiniMessage miniMessage;
    
    public JoinLeaveListener(ProxyServer server, Logger logger, ChatManager chatManager, DiscordManager discordManager, Config config, ServerGroupsRegistry serverGroupsRegistry) {
        this.server = server;
        this.logger = logger;
        this.chatManager = chatManager;
        this.discordManager = discordManager;
        this.config = config;
        this.miniMessage = MiniMessage.miniMessage();
        this.serverGroupsRegistry = serverGroupsRegistry;
    }
    
    @Subscribe(order = PostOrder.LAST)
    public void onPlayerJoin(PostLoginEvent event) {
        if (!config.isJoinLeaveEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        String senderServerName = player.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("");

        // Only broadcast plugin custom messages if group/config indicates custom events are handled
        boolean shouldHandle = serverGroupsRegistry == null || serverGroupsRegistry.shouldHandleEvent(senderServerName, MessageType.JOIN);

        Component joinMessage = null;
        if (shouldHandle) {
            // Prefer group-specific join message if present
            String format = serverGroupsRegistry != null ? serverGroupsRegistry.getJoinMessageForServer(senderServerName).orElse(config.getJoinMessage()) : config.getJoinMessage();
            String prefix = PrefixUtils.getPrefixString(player);
            String suffix = PrefixUtils.getSuffixString(player);
            String formattedMessage = format
                    .replace("{player}", player.getUsername())
                    .replace("{prefix}", prefix)
                    .replace("{suffix}", suffix)
                    .replace("%player%", player.getUsername())
                    .replace("%server%", senderServerName);
            joinMessage = miniMessage.deserialize(formattedMessage);

            for (Player onlinePlayer : server.getAllPlayers()) {
                try {
                    String recipientServerName = onlinePlayer.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("");
                    if (serverGroupsRegistry == null || serverGroupsRegistry.shouldDeliver(senderServerName, recipientServerName, MessageType.JOIN)) {
                        onlinePlayer.sendMessage(joinMessage);
                    }
                } catch (Exception e) {
                    onlinePlayer.sendMessage(joinMessage);
                }
            }
            logger.info("{} joined the network with custom message", player.getUsername());
        } else {
            logger.debug("Skipping plugin join broadcast for {} (vanilla/event handled elsewhere)", player.getUsername());
        }

        // Send join to Discord independently if enabled (respecting group discord flag)
        sendJoinMessageToDiscord(player.getUsername(), senderServerName);
    }
    
    @Subscribe(order = PostOrder.LAST)
    public void onPlayerLeave(DisconnectEvent event) {
        if (!config.isJoinLeaveEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        String senderServerName = player.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("");

        boolean shouldHandle = serverGroupsRegistry == null || serverGroupsRegistry.shouldHandleEvent(senderServerName, MessageType.LEAVE);

        if (shouldHandle) {
            String format = serverGroupsRegistry != null ? serverGroupsRegistry.getLeaveMessageForServer(senderServerName).orElse(config.getLeaveMessage()) : config.getLeaveMessage();
            String prefix = PrefixUtils.getPrefixString(player);
            String suffix = PrefixUtils.getSuffixString(player);
            String formattedMessage = format
                    .replace("{player}", player.getUsername())
                    .replace("{prefix}", prefix)
                    .replace("{suffix}", suffix)
                    .replace("%player%", player.getUsername())
                    .replace("%server%", senderServerName);
            Component leaveMessage = miniMessage.deserialize(formattedMessage);

            for (Player onlinePlayer : server.getAllPlayers()) {
                if (!onlinePlayer.equals(player)) {
                    try {
                        String recipientServerName = onlinePlayer.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("");
                        if (serverGroupsRegistry == null || serverGroupsRegistry.shouldDeliver(senderServerName, recipientServerName, MessageType.LEAVE)) {
                            onlinePlayer.sendMessage(leaveMessage);
                        }
                    } catch (Exception e) {
                        onlinePlayer.sendMessage(leaveMessage);
                    }
                }
            }
            logger.info("{} left the network with custom message", player.getUsername());
        } else {
            logger.debug("Skipping plugin leave broadcast for {} (vanilla/event handled elsewhere)", player.getUsername());
        }

        sendLeaveMessageToDiscord(player.getUsername(), senderServerName);

        // Clean up any player data
        if (chatManager != null) {
            chatManager.removePlayer(player);
        }
    }
    
    private Component createCustomJoinMessage(Player player) {
        String format = config.getJoinMessage();
        String prefix = PrefixUtils.getPrefixString(player);
        String suffix = PrefixUtils.getSuffixString(player);
        
        // Debug logging to ensure we're getting the right values
        logger.debug("Creating join message for {}: prefix='{}', suffix='{}'", 
                    player.getUsername(), prefix, suffix);
        
        // Replace placeholders
        String formattedMessage = format
                .replace("{player}", player.getUsername())
                .replace("{prefix}", prefix)
                .replace("{suffix}", suffix);
        
        logger.debug("Formatted join message: {}", formattedMessage);
        
        // Parse with MiniMessage to ensure proper color formatting
        Component message = miniMessage.deserialize(formattedMessage);
        
        return message;
    }
    
    private Component createCustomLeaveMessage(Player player) {
        String format = config.getLeaveMessage();
        String prefix = PrefixUtils.getPrefixString(player);
        String suffix = PrefixUtils.getSuffixString(player);
        
        // Debug logging to ensure we're getting the right values
        logger.debug("Creating leave message for {}: prefix='{}', suffix='{}'", 
                    player.getUsername(), prefix, suffix);
        
        // Replace placeholders
        String formattedMessage = format
                .replace("{player}", player.getUsername())
                .replace("{prefix}", prefix)
                .replace("{suffix}", suffix);
        
        logger.debug("Formatted leave message: {}", formattedMessage);
        
        // Parse with MiniMessage to ensure proper color formatting
        Component message = miniMessage.deserialize(formattedMessage);
        
        return message;
    }

    public void sendJoinMessageToDiscord(String playerName, String senderServerName) {
        if (discordManager != null && discordManager.isConnected()) {
            String discordFormat = config.getGameToDiscordJoinFormat();

            String discordMessage = discordFormat
                    .replace("{player}", playerName)
                    .replace("{server}", senderServerName == null ? "" : senderServerName);

            try {
                boolean global = config.isSendAllMessagesToDiscord();
                boolean groupAllows = true;
                if (serverGroupsRegistry != null) groupAllows = serverGroupsRegistry.shouldSendServerToDiscord(senderServerName);
                if (global && groupAllows) discordManager.sendToDiscord(discordMessage);
            } catch (Exception e) {
                logger.warn("Error while evaluating discord forwarding rules for join message", e);
            }
        }
    }

    public void sendLeaveMessageToDiscord(String playerName, String senderServerName) {
        if (discordManager != null && discordManager.isConnected()) {
            String discordFormat = config.getGameToDiscordLeaveFormat();

            String discordMessage = discordFormat
                    .replace("{player}", playerName)
                    .replace("{server}", senderServerName == null ? "" : senderServerName);

            try {
                boolean global = config.isSendAllMessagesToDiscord();
                boolean groupAllows = true;
                if (serverGroupsRegistry != null) groupAllows = serverGroupsRegistry.shouldSendServerToDiscord(senderServerName);
                if (global && groupAllows) discordManager.sendToDiscord(discordMessage);
            } catch (Exception e) {
                logger.warn("Error while evaluating discord forwarding rules for leave message", e);
            }
        }
    }
}
