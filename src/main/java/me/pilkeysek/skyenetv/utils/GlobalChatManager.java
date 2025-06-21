package me.pilkeysek.skyenetv.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalChatManager {
    
    private final ProxyServer server;
    private final Logger logger;
    private final DiscordManager discordManager;
    private final Config config;
    private final Map<UUID, Boolean> globalChatToggle;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public GlobalChatManager(ProxyServer server, Logger logger, DiscordManager discordManager, Config config) {
        this.server = server;
        this.logger = logger;
        this.discordManager = discordManager;
        this.config = config;
        this.globalChatToggle = new HashMap<>();
    }
    
    public boolean isGlobalChatEnabled(Player player) {
        return globalChatToggle.getOrDefault(player.getUniqueId(), config.isGlobalChatDefaultEnabled());
    }
    
    public void setGlobalChat(Player player, boolean enabled) {
        globalChatToggle.put(player.getUniqueId(), enabled);
    }
    
    public boolean toggleGlobalChat(Player player) {
        boolean currentState = isGlobalChatEnabled(player);
        boolean newState = !currentState;
        setGlobalChat(player, newState);
        return newState;
    }
    
    public void sendGlobalMessage(Player sender, String message) {
        String playerName = sender.getUsername();

        // Add globe emoji since this is a global message (sender has global chat enabled)
        // Other players will see this if they have global chat enabled or are on the same server
        String luckPermsPrefix = PrefixUtils.getPrefixString(sender);
        String luckPermsSuffix = PrefixUtils.getSuffixString(sender);

        // Create the global chat message using configurable format (with globe emoji)
        String formatTemplate = config.getGlobalChatFormat();
        String formattedMessage = formatTemplate
                .replace("{prefix}", luckPermsPrefix)
                .replace("{player}", playerName)
                .replace("{suffix}", luckPermsSuffix)
                .replace("{message}", message);

        Component globalMessage = miniMessage.deserialize(formattedMessage);

        // Get sender's current server
        String senderServerName = sender.getCurrentServer()
                .map(serverConnection -> serverConnection.getServerInfo().getName())
                .orElse("");

        // Send to players based on THEIR global chat settings
        for (Player recipient : server.getAllPlayers()) {
            // If recipient has global chat ON, they see all messages
            // If recipient has global chat OFF, they only see messages from same server
            if (isGlobalChatEnabled(recipient) || isSameServer(sender, recipient)) {
                recipient.sendMessage(globalMessage);
            }
        }

        logger.info("[Global Chat] {} (from {}): {}", sender.getUsername(), senderServerName, message);
    }
    
    public void sendLocalMessage(Player sender, String message) {
        String playerName = sender.getUsername();

        // Retrieve LuckPerms prefix and suffix using PrefixUtils
        String luckPermsPrefix = PrefixUtils.getPrefixString(sender);
        String luckPermsSuffix = PrefixUtils.getSuffixString(sender);

        // Create local message format (without globe emoji)
        String formatTemplate = "{prefix}{player}{suffix}: <white>{message}";
        String formattedMessage = formatTemplate
                .replace("{prefix}", luckPermsPrefix)
                .replace("{player}", playerName)
                .replace("{suffix}", luckPermsSuffix)
                .replace("{message}", message);

        Component localMessage = miniMessage.deserialize(formattedMessage);

        // Get sender's current server
        String senderServerName = sender.getCurrentServer()
                .map(serverConnection -> serverConnection.getServerInfo().getName())
                .orElse("");

        // Send only to players on the same server
        for (Player recipient : server.getAllPlayers()) {
            if (isSameServer(sender, recipient)) {
                recipient.sendMessage(localMessage);
            }
        }

        logger.info("[Local Chat] {} (from {}): {}", sender.getUsername(), senderServerName, message);
    }
    
    private boolean isSameServer(Player player1, Player player2) {
        String server1 = player1.getCurrentServer()
                .map(serverConnection -> serverConnection.getServerInfo().getName())
                .orElse("");
        String server2 = player2.getCurrentServer()
                .map(serverConnection -> serverConnection.getServerInfo().getName())
                .orElse("");
        return server1.equals(server2) && !server1.isEmpty();
    }
    
    /**
     * Process a player's chat message based on the channel system.
     * Always handles the message through SkyeNet formatting.
     * 
     * @param player The player sending the message
     * @param message The message content
     */
    public void processPlayerMessage(Player player, String message) {
        if (isGlobalChatEnabled(player)) {
            // Player has global chat ON - send with globe emoji, visible based on recipient settings
            sendGlobalMessage(player, message);
        } else {
            // Player has global chat OFF - send without globe emoji, only to same server
            sendLocalMessage(player, message);
        }
        
        // Send to Discord if configured
        sendMessageToDiscord(player, message);
    }
    
    public void sendMessageToDiscord(Player sender, String message) {
        if (discordManager != null && discordManager.isConnected()) {
            String discordFormat = config.getGameToDiscordFormat();
            String discordMessage = discordFormat
                    .replace("{player}", sender.getUsername())
                    .replace("{message}", message);

            // Include server name in the Discord message
            String serverName = sender.getCurrentServer().map(server -> server.getServerInfo().getName()).orElse("Unknown");
            discordMessage = discordMessage.replace("{server}", serverName);

            if (config.isSendAllMessagesToDiscord() || isGlobalChatEnabled(sender)) {
                discordManager.sendToDiscord(discordMessage);
            }
        }
    }

    
    public void removePlayer(Player player) {
        globalChatToggle.remove(player.getUniqueId());
    }
}
