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
    private final MiniMessage miniMessage;
    
    public JoinLeaveListener(ProxyServer server, Logger logger, ChatManager chatManager, DiscordManager discordManager, Config config) {
        this.server = server;
        this.logger = logger;
        this.chatManager = chatManager;
        this.discordManager = discordManager;
        this.config = config;
        this.miniMessage = MiniMessage.miniMessage();
    }
    
    @Subscribe(order = PostOrder.LAST)
    public void onPlayerJoin(PostLoginEvent event) {
        if (!config.isJoinLeaveEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Create custom join message with enhanced color formatting
        Component joinMessage = createCustomJoinMessage(player);
        
        // Broadcast to all players (this effectively replaces the vanilla message)
        for (Player onlinePlayer : server.getAllPlayers()) {
            onlinePlayer.sendMessage(joinMessage); // Pass Component directly
        }

        sendJoinMessageToDiscord(player.getUsername());
        
        logger.info("{} joined the network with custom message", player.getUsername());
    }
    
    @Subscribe(order = PostOrder.LAST)
    public void onPlayerLeave(DisconnectEvent event) {
        if (!config.isJoinLeaveEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Create custom leave message with enhanced color formatting
        Component leaveMessage = createCustomLeaveMessage(player);
        
        // Broadcast to all remaining players
        for (Player onlinePlayer : server.getAllPlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.sendMessage(leaveMessage); // Pass Component directly
            }
        }

        sendLeaveMessageToDiscord(player.getUsername());
        
        // Clean up any player data
        if (chatManager != null) {
            chatManager.removePlayer(player);
        }
        
        logger.info("{} left the network with custom message", player.getUsername());
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

    public void sendJoinMessageToDiscord(String playerName) {
        if (discordManager != null && discordManager.isConnected()) {
            String discordFormat = config.getGameToDiscordJoinFormat();

            String discordMessage = discordFormat
                    .replace("{player}", playerName);

            if (config.isSendAllMessagesToDiscord()) {
                discordManager.sendToDiscord(discordMessage);
            }
        }
    }

    public void sendLeaveMessageToDiscord(String playerName) {
        if (discordManager != null && discordManager.isConnected()) {
            String discordFormat = config.getGameToDiscordLeaveFormat();

            String discordMessage = discordFormat
                    .replace("{player}", playerName);

            if (config.isSendAllMessagesToDiscord()) {
                discordManager.sendToDiscord(discordMessage);
            }
        }
    }
}
