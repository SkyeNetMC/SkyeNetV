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
     * Create a fully formatted player name using MiniMessage format that preserves gradient/color continuation
     * This method parses the entire prefix+name+suffix as a single MiniMessage string to preserve gradients
     * @param player The player to format
     * @return Formatted component with proper color continuation
     */
    public static Component getFullFormattedNameWithColorContinuation(Player player) {
        if (luckPerms == null) {
            return Component.text(player.getUsername());
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return Component.text(player.getUsername());
            }

            CachedMetaData metaData = user.getCachedData().getMetaData();
            
            // Get raw prefix and suffix
            String prefix = metaData.getPrefix();
            String suffix = metaData.getSuffix();
            
            // Build a complete MiniMessage string that preserves color continuations
            StringBuilder fullFormat = new StringBuilder();
            
            if (prefix != null && !prefix.isEmpty()) {
                fullFormat.append(prefix);
            }
            
            // Add the player name - this will inherit colors from prefix if no explicit color is set
            fullFormat.append(player.getUsername());
            
            if (suffix != null && !suffix.isEmpty()) {
                fullFormat.append(suffix);
            }
            
            // Parse the entire string as MiniMessage to preserve gradients and color continuations
            return miniMessage.deserialize(fullFormat.toString());
            
        } catch (Exception e) {
            logger.warn("Failed to get formatted name with color continuation for player {}: {}", player.getUsername(), e.getMessage());
        }

        return Component.text(player.getUsername());
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

    /**
     * Create join message with [+] format and LuckPerms formatting
     * @param player The player who joined
     * @return Formatted join message component
     */
    public static Component createJoinMessage(Player player) {
        Component playerName = getFullFormattedName(player);
        
        return Component.text()
                .append(Component.text("[", net.kyori.adventure.text.format.NamedTextColor.GRAY))
                .append(Component.text("+", net.kyori.adventure.text.format.NamedTextColor.GREEN))
                .append(Component.text("] ", net.kyori.adventure.text.format.NamedTextColor.GRAY))
                .append(playerName)
                .append(Component.text(" joined the network", net.kyori.adventure.text.format.NamedTextColor.GREEN))
                .build();
    }

    /**
     * Create leave message with [-] format and LuckPerms formatting
     * @param player The player who left
     * @return Formatted leave message component
     */
    public static Component createLeaveMessage(Player player) {
        Component playerName = getFullFormattedName(player);
        
        return Component.text()
                .append(Component.text("[", net.kyori.adventure.text.format.NamedTextColor.GRAY))
                .append(Component.text("-", net.kyori.adventure.text.format.NamedTextColor.RED))
                .append(Component.text("] ", net.kyori.adventure.text.format.NamedTextColor.GRAY))
                .append(playerName)
                .append(Component.text(" left the network", net.kyori.adventure.text.format.NamedTextColor.RED))
                .build();
    }
}
