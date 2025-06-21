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

        // Retrieve LuckPerms prefix and suffix using PrefixUtils
        String luckPermsPrefix = PrefixUtils.getPrefixString(sender);
        String luckPermsSuffix = PrefixUtils.getSuffixString(sender);

        // Create the global chat message using configurable format
        String formatTemplate = config.getGlobalChatFormat();
        String formattedMessage = formatTemplate
                .replace("{prefix}", luckPermsPrefix)
                .replace("{player}", playerName)
                .replace("{suffix}", luckPermsSuffix)
                .replace("{message}", message);

        Component globalMessage = miniMessage.deserialize(formattedMessage);

        // Send the formatted global message to ALL players (including same server)
        // The ChatListener should cancel the original event to prevent duplication
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(globalMessage);
        }

        logger.info("[Global Chat] {}: {}", sender.getUsername(), message);
    }
    
    /**
     * Process a player's chat message based on their global chat settings.
     * 
     * @param player The player sending the message
     * @param message The message content
     * @return true if the message was sent to global chat (and should be cancelled), false if it should be handled normally
     */
    public boolean processPlayerMessage(Player player, String message) {
        if (isGlobalChatEnabled(player)) {
            // Send as global message only
            sendGlobalMessage(player, message);
            // Also send to Discord if configured
            sendMessageToDiscord(player, message);
            return true; // Message sent to global chat, should be cancelled locally
        } else {
            // Log normal chat messages in proxy
            logger.info("[Normal Chat] {}: {}", player.getUsername(), message);
            // Send to Discord if configured for all messages
            sendMessageToDiscord(player, message);
        }
        return false; // Message should be handled normally by the server
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
