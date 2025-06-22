package me.pilkeysek.skyenetv.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

public class ChatManager {
    
    private final ProxyServer server;
    private final Logger logger;
    private final DiscordManager discordManager;
    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public ChatManager(ProxyServer server, Logger logger, DiscordManager discordManager, Config config) {
        this.server = server;
        this.logger = logger;
        this.discordManager = discordManager;
        this.config = config;
    }
    
    /**
     * Process a player's chat message.
     * 
     * @param player The player sending the message
     * @param message The message content
     */
    public void processPlayerMessage(Player player, String message) {
        try {
            // Validate player connection
            if (!player.isActive()) {
                logger.warn("Attempted to process message for inactive player: {}", player.getUsername());
                return;
            }
            
            // Get player info
            String playerName = player.getUsername();
            String luckPermsPrefix = PrefixUtils.getPrefixString(player);
            String luckPermsSuffix = PrefixUtils.getSuffixString(player);
            
            // Get current server information for logging
            String senderServerName = player.getCurrentServer()
                    .map(serverConnection -> serverConnection.getServerInfo().getName())
                    .orElse("");
                    
            // Skip if sender server is unknown
            if (senderServerName.isEmpty()) {
                logger.warn("Could not determine server for player: {}", playerName);
                return;
            }
                
        // Create the chat format - simplified global format
        String formatTemplate = config.getString("chat.format", "{prefix}{player}{suffix}<gray>» {message}");
        
        // Format the message
        String formattedMessage = formatTemplate
                .replace("{prefix}", luckPermsPrefix)
                .replace("{player}", playerName)
                .replace("{suffix}", luckPermsSuffix)
                .replace("{message}", message);
        
        Component chatComponent = miniMessage.deserialize(formattedMessage);
        
        // Forward message to players on OTHER servers only
        // Do NOT send back to the same server - let the backend server handle local chat
        for (Player recipient : server.getAllPlayers()) {
            // Skip players on the same server as the sender
            String recipientServerName = recipient.getCurrentServer()
                    .map(serverConnection -> serverConnection.getServerInfo().getName())
                    .orElse("");
            
            if (recipientServerName.equals(senderServerName)) {
                continue; // Skip - let backend server handle local chat
            }
            
            // Send to players on different servers only
            recipient.sendMessage(chatComponent);
        }
        
        // Log the message
        logger.info("[Global Chat] {} (from {}): {}", player.getUsername(), senderServerName, message);
        
        // Send to Discord if configured
        sendMessageToDiscord(player, message);
        
        } catch (Exception e) {
            logger.error("Error processing player message for {}: {}", player.getUsername(), e.getMessage(), e);
        }
    }
    
    public void sendMessageToDiscord(Player sender, String message) {
        if (discordManager != null && discordManager.isConnected()) {
            String discordFormat = config.getGameToDiscordFormat();
            
            // Include server name in the Discord message
            String serverName = sender.getCurrentServer().map(server -> server.getServerInfo().getName()).orElse("Unknown");
            
            String discordMessage = discordFormat
                    .replace("{server}", serverName)
                    .replace("{prefix}", PrefixUtils.getPrefixString(sender))
                    .replace("{player}", sender.getUsername())
                    .replace("{suffix}", PrefixUtils.getSuffixString(sender))
                    .replace("{message}", message);

            if (config.isSendAllMessagesToDiscord()) {
                discordManager.sendToDiscord(discordMessage);
            }
        }
    }
    
    /**
     * Remove a player's data when they leave the server
     */
    public void removePlayer(Player player) {
        // Nothing to remove since we don't store per-player data
        // Method included for compatibility with existing listeners
    }
    
    /**
     * Helper method to retrieve String values from the config
     */
    // The format is already available via the config's getString method
}
