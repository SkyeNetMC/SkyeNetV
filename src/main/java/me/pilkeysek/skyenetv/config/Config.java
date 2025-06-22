package me.pilkeysek.skyenetv.config;

import com.velocitypowered.api.proxy.Player;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Config {
    
    private final Path dataDirectory;
    private final Logger logger;
    private final File configFile;
    private Map<String, Object> config;
    
    public Config(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.configFile = new File(dataDirectory.toFile(), "config.yml");
        loadConfig();
    }
    
    private void loadConfig() {
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        
        try {
            Yaml yaml = new Yaml();
            FileInputStream inputStream = new FileInputStream(configFile);
            config = yaml.load(inputStream);
            inputStream.close();
            
            if (config == null) {
                config = new HashMap<>();
            }
            
            logger.info("Configuration loaded successfully");
        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
            config = new HashMap<>();
        }
    }
    
    private void saveDefaultConfig() {
        try {
            // Try to copy from resources first
            InputStream defaultConfig = getClass().getResourceAsStream("/config.yml");
            if (defaultConfig != null) {
                Files.copy(defaultConfig, configFile.toPath());
                defaultConfig.close();
                logger.info("Created default configuration file from template");
                return;
            }
        } catch (Exception e) {
            logger.warn("Could not copy default config from resources, creating basic config");
        }
        
        // Fallback: create basic configuration
        createBasicConfig();
    }
    
    private void createBasicConfig() {
        try {
            Map<String, Object> defaultConfig = new HashMap<>();
            
            // Discord Settings
            Map<String, Object> discord = new HashMap<>();
            discord.put("enabled", false);
            discord.put("token", "YOUR_ACTUAL_BOT_TOKEN_HERE");
            discord.put("channel", "YOUR_ACTUAL_CHANNEL_ID_HERE");
            discord.put("global_chat_to_discord", true);
            discord.put("discord_to_game", true);
            discord.put("join_leave_to_discord", false);
            discord.put("game_to_discord_format", "**{player}**: {message}");
            discord.put("discord_to_game_format", "<gray>[Discord]</gray> <white><bold>{user}</bold>:</white> {message}");
            discord.put("send_all_messages_to_discord", false);
            discord.put("include_server_name", false);
            defaultConfig.put("discord", discord);
            
            // Global Chat Settings
            Map<String, Object> globalChat = new HashMap<>();
            globalChat.put("enabled", true);
            globalChat.put("format", "🌐 {prefix}{player}: <white>{message}");
            globalChat.put("default_enabled", false);
            globalChat.put("enabled_message", "<green>🌐 Global chat enabled! Your messages will be sent to all servers.</green>");
            globalChat.put("disabled_message", "<yellow>🌐 Global chat disabled! Your messages will only be sent to your current server.</yellow>");
            defaultConfig.put("global_chat", globalChat);
            
            // Join/Leave Settings
            Map<String, Object> joinLeave = new HashMap<>();
            joinLeave.put("enabled", true);
            joinLeave.put("suppress_vanilla", true);
            joinLeave.put("join_format", "[<green>+</green>] {prefix}{player}{suffix} <green>joined the network</green>");
            joinLeave.put("leave_format", "[<red>-</red>] {prefix}{player}{suffix} <red>left the network</red>");
            defaultConfig.put("join_leave", joinLeave);

            // Per-Server Settings
            Map<String, Object> perServer = new HashMap<>();
            perServer.put("enabled", true);

            Map<String, Object> joinMessages = new HashMap<>();
            joinMessages.put("network", "<dark_gray>[<green>+<dark_gray>] {prefix}{player} <dark_gray>has joined the network!");
            joinMessages.put("server", "<dark_gray>[<green>+<dark_gray>] {prefix}{player} <dark_gray>has joined {server}!");
            perServer.put("join_messages", joinMessages);

            Map<String, Object> leaveMessages = new HashMap<>();
            leaveMessages.put("network", "<dark_gray>[<red>-<dark_gray>] {prefix}{player} <dark_gray>has left the network!");
            leaveMessages.put("server", "<dark_gray>[<red>-<dark_gray>] {prefix}{player} <dark_gray>has left {server}!");
            perServer.put("leave_messages", leaveMessages);

            defaultConfig.put("per_server", perServer);

            // Messages
            Map<String, Object> messages = new HashMap<>();
            messages.put("local_chat_enabled", "<green>💬 You are now in local chat mode. Your messages will only be sent to your current server.</green>");
            messages.put("discord_info", "<blue>🔗 Discord Integration</blue>");
            messages.put("discord_invite", "<green>Join our Discord server!</green>");
            messages.put("discord_online", "<green>✅ Discord bot is online and connected!</green>");
            messages.put("discord_offline", "<red>❌ Discord bot is offline.</red>");
            messages.put("no_permission", "<red>You don't have permission to use this command!</red>");
            messages.put("player_only", "<red>Only players can use this command!</red>");
            // Private message formats
            messages.put("private_message_format", "<gray>[PM]</gray> <white>{sender}</white>: {message}");
            messages.put("private_message_sent_format", "<gray>[PM]</gray> <white>You</white>: {message}");
            defaultConfig.put("messages", messages);
            
            // Server Settings
            Map<String, Object> server = new HashMap<>();
            server.put("lobby_server", "lobby");
            
            // Lobby spawn coordinates
            Map<String, Object> lobbySpawn = new HashMap<>();
            lobbySpawn.put("x", 0.5);
            lobbySpawn.put("y", 100.0);
            lobbySpawn.put("z", 0.5);
            lobbySpawn.put("yaw", 0.0);
            lobbySpawn.put("pitch", 0.0);
            server.put("lobby_spawn", lobbySpawn);
            
            defaultConfig.put("server", server);
            
            // Plugin Settings
            Map<String, Object> plugin = new HashMap<>();
            plugin.put("debug", false);
            defaultConfig.put("plugin", plugin);
            
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(configFile);
            yaml.dump(defaultConfig, writer);
            writer.close();
            
            logger.info("Created basic configuration file at: {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to create default configuration", e);
        }
    }
    
    // Global Chat Methods
    public boolean isGlobalChatEnabled() {
        return getBoolean("global_chat.enabled", true);
    }
    
    public String getGlobalChatFormat() {
        return getString("global_chat.format", "🌐 {prefix}{player}: {message}");
    }
    
    public boolean isGlobalChatDefaultEnabled() {
        return getBoolean("global_chat.default_enabled", false);
    }
    
    // Discord Methods
    public boolean isDiscordEnabled() {
        return getBoolean("discord.enabled", false);
    }
    
    public String getDiscordBotToken() {
        return getString("discord.token", "");
    }
    
    public String getDiscordChatChannelId() {
        return getString("discord.channel", "");
    }
    
    public boolean isGlobalChatToDiscordEnabled() {
        return getBoolean("discord.global_chat_to_discord", true);
    }
    
    public boolean isDiscordToGameEnabled() {
        return getBoolean("discord.discord_to_game", true);
    }
    
    public boolean isJoinLeaveToDiscordEnabled() {
        return getBoolean("discord.join_leave_to_discord", false);
    }
    
    public boolean isSendAllMessagesToDiscord() {
        return getBoolean("discord.send_all_messages_to_discord", false);
    }
    
    public boolean isIncludeServerNameInDiscordMessage() {
        return getBoolean("discord.include_server_name", false);
    }
    
    // Join/Leave Methods
    public boolean isJoinLeaveEnabled() {
        return getBoolean("join_leave.enabled", true);
    }
    
    public boolean isSuppressVanillaJoinLeave() {
        return getBoolean("join_leave.suppress_vanilla", true);
    }
    
    public String getJoinMessage() {
        return getString("join_leave.join_format", "[<green>+</green>] {prefix}{player}{suffix} <green>joined the network</green>");
    }
    
    public String getLeaveMessage() {
        return getString("join_leave.leave_format", "[<red>-</red>] {prefix}{player}{suffix} <red>left the network</red>");
    }
    
    public String getLeaveMessage(String type) {
        Object message = config.get("per_server.leave_messages." + type);
        return message != null ? message.toString() : "";
    }
    
    // Message Methods
    public String getGlobalChatEnabledMessage() {
        return getString("global_chat.enabled_message", "<green>🌐 Global chat enabled! Your messages will be sent to all servers.</green>");
    }
    
    public String getGlobalChatDisabledMessage() {
        return getString("global_chat.disabled_message", "<yellow>🌐 Global chat disabled! Your messages will only be sent to your current server.</yellow>");
    }
    
    public String getLocalChatEnabledMessage() {
        return getString("messages.local_chat_enabled", "<green>💬 You are now in local chat mode. Your messages will only be sent to your current server.</green>");
    }
    
    public String getDiscordInfoMessage() {
        return getString("messages.discord_info", "<blue>🔗 Discord Integration</blue>");
    }
    
    public String getDiscordInviteMessage() {
        return getString("messages.discord_invite", "<green>Join our Discord server!</green>");
    }
    
    public String getDiscordStatusOnlineMessage() {
        return getString("messages.discord_online", "<green>✅ Discord bot is online and connected!</green>");
    }
    
    public String getDiscordStatusOfflineMessage() {
        return getString("messages.discord_offline", "<red>❌ Discord bot is offline.</red>");
    }
    
    public String getNoPermissionMessage() {
        return getString("messages.no_permission", "<red>You don't have permission to use this command!</red>");
    }
    
    public String getPlayerOnlyMessage() {
        return getString("messages.player_only", "<red>Only players can use this command!</red>");
    }
    
    // Discord Message Formats
    public String getGameToDiscordFormat() {
        return getString("discord.game_to_discord_format", "**{player}**: {message}");
    }
    
    public String getDiscordToGameFormat() {
        return getString("discord.discord_to_game_format", "<gray>[Discord]</gray> <white><bold>{user}</bold>:</white> {message}");
    }
    
    // Server Methods
    public String getServerJoinMessage(String serverName) {
        return getString("servers." + serverName + ".join_message", "");
    }
    
    // Utility Methods
    public String getString(String path, String defaultValue) {
        return getValueAtPath(path, String.class, defaultValue);
    }
    
    public boolean getBoolean(String path, boolean defaultValue) {
        return getValueAtPath(path, Boolean.class, defaultValue);
    }
    
    public int getInt(String path, int defaultValue) {
        return getValueAtPath(path, Integer.class, defaultValue);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getValueAtPath(String path, Class<T> type, T defaultValue) {
        try {
            String[] keys = path.split("\\.");
            Object current = config;
            
            for (String key : keys) {
                if (current instanceof Map) {
                    current = ((Map<String, Object>) current).get(key);
                } else {
                    return defaultValue;
                }
                
                if (current == null) {
                    return defaultValue;
                }
            }
            
            if (type.isInstance(current)) {
                return type.cast(current);
            }
        } catch (Exception e) {
            logger.warn("Error getting config value at path '{}': {}", path, e.getMessage());
        }
        
        return defaultValue;
    }
    
    /**
     * Reloads the configuration from disk
     * @return true if reload was successful, false otherwise
     */
    public boolean reload() {
        try {
            Yaml yaml = new Yaml();
            FileInputStream inputStream = new FileInputStream(configFile);
            config = yaml.load(inputStream);
            inputStream.close();
            
            if (config == null) {
                config = new HashMap<>();
            }
            
            logger.info("Configuration reloaded successfully");
            return true;
        } catch (Exception e) {
            logger.error("Failed to reload configuration", e);
            return false;
        }
    }
    
    public Map<String, Object> getConfigMap() {
        return new HashMap<>(config);
    }
    
    // Add method to retrieve LuckPerms prefix
    public String getLuckPermsPrefix(Player player) {
        // Placeholder logic to retrieve LuckPerms prefix
        return "<prefix>"; // Replace with actual LuckPerms API call
    }
    
    // Private Message Formats
    public String getPrivateMessageFormat() {
        return getString("messages.private_message_format", "<gray>[PM]</gray> <white>{sender}</white>: {message}");
    }

    public String getPrivateMessageSentFormat() {
        return getString("messages.private_message_sent_format", "<gray>[PM]</gray> <white>You</white>: {message}");
    }
    
    // Lobby Configuration Methods
    public String getLobbyServerName() {
        return getString("server.lobby_server", "lobby");
    }
    
    public double getLobbySpawnX() {
        return getDouble("server.lobby_spawn.x", 0.5);
    }
    
    public double getLobbySpawnY() {
        return getDouble("server.lobby_spawn.y", 100.0);
    }
    
    public double getLobbySpawnZ() {
        return getDouble("server.lobby_spawn.z", 0.5);
    }
    
    public float getLobbySpawnYaw() {
        return (float) getDouble("server.lobby_spawn.yaw", 0.0);
    }
    
    public float getLobbySpawnPitch() {
        return (float) getDouble("server.lobby_spawn.pitch", 0.0);
    }
    
    // Helper method for double values
    public double getDouble(String path, double defaultValue) {
        Object value = getValueAtPath(path, Object.class, defaultValue);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
