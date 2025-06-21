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

    /**
     * Process a player's chat message.
     * 
     * @param player The player sending the message
     * @param message The message content
     * @param forceGlobal If true, treat as global chat regardless of player's toggle state
     */
    public void processPlayerMessage(Player player, String message, boolean forceGlobal) {
        boolean isGlobalChat = forceGlobal || isGlobalChatEnabled(player);
        
        // Get player info
        String playerName = player.getUsername();
        String luckPermsPrefix = PrefixUtils.getPrefixString(player);
        String luckPermsSuffix = PrefixUtils.getSuffixString(player);
        
        // Get current server information for routing and logging
        String senderServerName = player.getCurrentServer()
                .map(serverConnection -> serverConnection.getServerInfo().getName())
                .orElse("");
                
        // Create appropriate message formats
        String formatTemplate = config.getGlobalChatFormat();
        String localFormatTemplate = formatTemplate.replace("🌐", "").trim();
        
        // Format global message (with emoji)
        String globalFormattedMessage = formatTemplate
                .replace("{prefix}", luckPermsPrefix)
                .replace("{player}", playerName)
                .replace("{suffix}", luckPermsSuffix)
                .replace("{message}", message);
                
        // Format local message (without emoji)
        String localFormattedMessage = localFormatTemplate
                .replace("{prefix}", luckPermsPrefix)
                .replace("{player}", playerName)
                .replace("{suffix}", luckPermsSuffix)
                .replace("{message}", message);
        
        Component globalComponent = miniMessage.deserialize(globalFormattedMessage);
        Component localComponent = miniMessage.deserialize(localFormattedMessage);
        
        // IMPORTANT: Send formatted message to the sender first
        // Since we cancel the vanilla event, sender needs to see their own message
        if (isGlobalChat) {
            player.sendMessage(globalComponent);
        } else {
            player.sendMessage(localComponent);
        }
        
        // For every player on the proxy (except sender)
        for (Player recipient : server.getAllPlayers()) {
            // Skip the sender since we already sent them their own message
            if (recipient.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            
            boolean recipientHasGlobalOn = isGlobalChatEnabled(recipient);
            boolean sameServer = isSameServer(player, recipient);
            
            if (isGlobalChat) {
                // CASE: Sender has global chat ON (or forced)
                if (recipientHasGlobalOn) {
                    // Send global format (with emoji) to all players with global chat ON
                    recipient.sendMessage(globalComponent);
                } else if (sameServer) {
                    // Send local format (no emoji) to players on same server with global chat OFF
                    recipient.sendMessage(localComponent);
                }
            } else {
                // CASE: Sender has global chat OFF
                if (sameServer) {
                    // Only send local format to players on same server
                    recipient.sendMessage(localComponent);
                }
                // Players on other servers don't receive the message
            }
        }
        
        // Log the message
        String chatType = isGlobalChat ? "Global" : "Local";
        logger.info("[{} Chat] {} (from {}): {}", chatType, player.getUsername(), senderServerName, message);
        
        // Send to Discord if configured
        sendMessageToDiscord(player, message);
    }

    /**
     * Process a normal chat message based on player's current global chat setting
     */
    public void processPlayerMessage(Player player, String message) {
        processPlayerMessage(player, message, false);
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
