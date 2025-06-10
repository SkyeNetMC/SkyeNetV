package me.pilkeysek.skyenetv.utils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.slf4j.Logger;

public class PrefixUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
    private static LuckPerms luckPerms;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PrefixUtils.class);

    public static void initialize() {
        try {
            luckPerms = LuckPermsProvider.get();
            logger.info("LuckPerms integration initialized successfully!");
        } catch (Exception e) {
            logger.warn("LuckPerms not found. Prefix functionality will be disabled.");
            luckPerms = null;
        }
    }

    /**
     * Get the formatted prefix for a player from LuckPerms
     * @param player The player to get the prefix for
     * @return Component with the formatted prefix, or empty component if no prefix
     */
    public static Component getPlayerPrefix(Player player) {
        if (luckPerms == null) {
            return Component.empty();
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return Component.empty();
            }

            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getPrefix();
            
            if (prefix != null && !prefix.isEmpty()) {
                // Use MiniMessage to parse the prefix (supports color codes and formatting)
                return miniMessage.deserialize(prefix);
            }
        } catch (Exception e) {
            logger.warn("Failed to get prefix for player {}: {}", player.getUsername(), e.getMessage());
        }

        return Component.empty();
    }

    /**
     * Get the formatted suffix for a player from LuckPerms
     * @param player The player to get the suffix for
     * @return Component with the formatted suffix, or empty component if no suffix
     */
    public static Component getPlayerSuffix(Player player) {
        if (luckPerms == null) {
            return Component.empty();
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return Component.empty();
            }

            CachedMetaData metaData = user.getCachedData().getMetaData();
            String suffix = metaData.getSuffix();
            
            if (suffix != null && !suffix.isEmpty()) {
                // Use MiniMessage to parse the suffix (supports color codes and formatting)
                return miniMessage.deserialize(suffix);
            }
        } catch (Exception e) {
            logger.warn("Failed to get suffix for player {}: {}", player.getUsername(), e.getMessage());
        }

        return Component.empty();
    }

    /**
     * Get the primary group color for a player from LuckPerms
     * @param player The player to get the color for
     * @return Component with the colored username, or white username if no color
     */
    public static Component getColoredPlayerName(Player player) {
        if (luckPerms == null) {
            return Component.text(player.getUsername());
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return Component.text(player.getUsername());
            }

            CachedMetaData metaData = user.getCachedData().getMetaData();
            
            // Try to get name color from meta
            String nameColor = metaData.getMetaValue("name-color");
            if (nameColor != null && !nameColor.isEmpty()) {
                return miniMessage.deserialize("<" + nameColor + ">" + player.getUsername() + "</" + nameColor + ">");
            }
            
            // Fallback to group color if available
            String primaryGroup = metaData.getPrimaryGroup();
            if (primaryGroup != null) {
                // Try to get group-specific color formatting
                String groupColor = metaData.getMetaValue("group-color");
                if (groupColor != null && !groupColor.isEmpty()) {
                    return miniMessage.deserialize("<" + groupColor + ">" + player.getUsername() + "</" + groupColor + ">");
                }
            }
            
        } catch (Exception e) {
            logger.warn("Failed to get colored name for player {}: {}", player.getUsername(), e.getMessage());
        }

        return Component.text(player.getUsername());
    }

    /**
     * Create a fully formatted player name with prefix, colored name, and suffix
     * @param player The player to format
     * @return Formatted component with prefix, colored name, and suffix
     */
    public static Component getFullFormattedName(Player player) {
        Component prefix = getPlayerPrefix(player);
        Component coloredName = getColoredPlayerName(player);
        Component suffix = getPlayerSuffix(player);
        
        return Component.text()
                .append(prefix)
                .append(coloredName)
                .append(suffix)
                .build();
    }

    /**
     * Check if LuckPerms is available
     * @return true if LuckPerms is loaded and available
     */
    public static boolean isLuckPermsAvailable() {
        return luckPerms != null;
    }

    /**
     * Get the LuckPerms prefix as plain text for message placeholders
     * @param player The player to get the prefix for
     * @return Plain text prefix, or empty string if no prefix
     */
    public static String getPlayerPrefixText(Player player) {
        if (luckPerms == null) {
            return "";
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return "";
            }

            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getPrefix();
            
            if (prefix != null && !prefix.isEmpty()) {
                // Convert MiniMessage formatted prefix to plain text
                Component prefixComponent = miniMessage.deserialize(prefix);
                return plainSerializer.serialize(prefixComponent);
            }
        } catch (Exception e) {
            logger.warn("Failed to get prefix text for player {}: {}", player.getUsername(), e.getMessage());
        }

        return "";
    }
}
