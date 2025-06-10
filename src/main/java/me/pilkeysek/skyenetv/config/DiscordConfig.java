package me.pilkeysek.skyenetv.config;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DiscordConfig {
    private final Path dataDirectory;
    private final Logger logger;
    private Map<String, Object> config;
    
    // Default values
    private String token = "YOUR_BOT_TOKEN_HERE";
    private String channelId = "YOUR_CHANNEL_ID_HERE";
    private boolean showPrefixes = true;
    private boolean enableJoinLeave = true;
    private boolean enableServerSwitch = true;
    
    // Global chat Discord integration
    private boolean onlyGlobalChatToDiscord = true;
    private java.util.List<String> disabledServers = new java.util.ArrayList<>();
    
    // Network join/leave configuration
    private boolean broadcastJoinToAllServers = true;
    private boolean broadcastLeaveToAllServers = true;
    private boolean showServerTransfers = false;
    private String networkJoinFormat = "<green>✅ <bold>{player}</bold> joined the network!</green>";
    private String networkLeaveFormat = "<red>❌ <bold>{player}</bold> left the network!</red>";
    
    // Discord name format configuration
    private String discordNameFormat = "username"; // "username" or "displayname"
    private String discordMessageFormat = "<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}";
    
    // Bot activity configuration
    private String botActivityStatus = "ONLINE";
    private String botActivityType = "CUSTOM_STATUS";
    private String botActivityText = "%online%/%max-players% - play.skyenet.co.in";
    
    // MiniMessage formats
    private String joinMessage = "<green>✅ <bold>{player}</bold> joined the network!</green>";
    private String leaveMessage = "<red>❌ <bold>{player}</bold> left the network!</red>";
    private String serverSwitchMessage = "<yellow>🔄 <bold>{player}</bold> switched from <italic>{from}</italic> to <italic>{to}</italic></yellow>";
    private String chatPrefix = "<gray>[<blue>{server}</blue>]</gray> <white><bold>{player}</bold>:</white> ";

    // Global Chat message formats
    private String globalChatMessageWithIcon = "🌐 {luckperms_prefix}<bold>{player}</bold>: {message}";
    private String globalChatMessageWithoutIcon = "{luckperms_prefix}<bold>{player}</bold>: {message}";
    private String globalChatJoinMessage = "🌐 {luckperms_prefix}<bold>{player}</bold> <green>joined global chat</green>";
    private String globalChatLeaveMessage = "🌐 {luckperms_prefix}<bold>{player}</bold> <red>left global chat</red>";
    private String globalChatNewPlayerNotification = "<green>You are not connected to global chat. Type </green><gold><bold>/gc</bold></gold><green> to toggle.</green>";
    
    public DiscordConfig(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        loadConfig();
    }
    
    private void loadConfig() {
        File configFile = new File(dataDirectory.toFile(), "config.yml");
        
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }
        
        try {
            Yaml yaml = new Yaml();
            FileInputStream fis = new FileInputStream(configFile);
            config = yaml.load(fis);
            fis.close();
            
            if (config == null) {
                config = new HashMap<>();
            }
            
            // Load values from config
            token = getString("discord.token", token);
            channelId = getString("discord.channel", channelId);
            showPrefixes = getBoolean("discord.show_prefixes", showPrefixes);
            enableJoinLeave = getBoolean("discord.enable_join_leave", enableJoinLeave);
            enableServerSwitch = getBoolean("discord.enable_server_switch", enableServerSwitch);
            
            // Load network configuration
            broadcastJoinToAllServers = getBoolean("network.broadcast_join_to_all_servers", broadcastJoinToAllServers);
            broadcastLeaveToAllServers = getBoolean("network.broadcast_leave_to_all_servers", broadcastLeaveToAllServers);
            showServerTransfers = getBoolean("network.show_server_transfers", showServerTransfers);
            networkJoinFormat = getString("network.join_format", networkJoinFormat);
            networkLeaveFormat = getString("network.leave_format", networkLeaveFormat);
            
            // Load global chat Discord integration settings
            onlyGlobalChatToDiscord = getBoolean("discord.only_global_chat_to_discord", onlyGlobalChatToDiscord);
            disabledServers = getList("discord.disabled_servers", disabledServers);
            
            // Load Discord name format configuration
            discordNameFormat = getString("discord.name_format", discordNameFormat);
            discordMessageFormat = getString("discord.message_format", discordMessageFormat);
            
            // Load bot activity configuration
            botActivityStatus = getString("bot-activity.status", botActivityStatus);
            botActivityType = getString("bot-activity.type", botActivityType);
            botActivityText = getString("bot-activity.text", botActivityText);
            
            // Load MiniMessage formats
            joinMessage = getString("messages.join", joinMessage);
            leaveMessage = getString("messages.leave", leaveMessage);
            serverSwitchMessage = getString("messages.server_switch", serverSwitchMessage);
            chatPrefix = getString("messages.chat_prefix", chatPrefix);
            
            // Load global chat message formats
            globalChatMessageWithIcon = getString("global_chat.message_with_icon", globalChatMessageWithIcon);
            globalChatMessageWithoutIcon = getString("global_chat.message_without_icon", globalChatMessageWithoutIcon);
            globalChatJoinMessage = getString("global_chat.join_message", globalChatJoinMessage);
            globalChatLeaveMessage = getString("global_chat.leave_message", globalChatLeaveMessage);
            globalChatNewPlayerNotification = getString("global_chat.new_player_notification", globalChatNewPlayerNotification);
            
            logger.info("Discord configuration loaded successfully!");
            
        } catch (Exception e) {
            logger.error("Failed to load configuration: " + e.getMessage());
            createDefaultConfig(configFile);
        }
    }
    
    private void createDefaultConfig(File configFile) {
        try {
            String content = """
                # SkyeNetV Configuration
                # Use MiniMessage format for colored text: https://docs.advntr.dev/minimessage/format.html
                
                discord:
                  # Your Discord bot token (get from https://discord.com/developers/applications)
                  token: "YOUR_BOT_TOKEN_HERE"
                  
                  # Discord channel ID where messages will be sent
                  channel: "YOUR_CHANNEL_ID_HERE"
                  
                  # Show server prefixes in chat messages
                  show_prefixes: true
                  
                  # Enable join/leave notifications
                  enable_join_leave: true
                  
                  # Enable server switch notifications
                  enable_server_switch: true
                  
                  # Only send global chat messages to Discord (if false, all chat goes to Discord)
                  only_global_chat_to_discord: true
                  
                  # List of servers where global chat is disabled and messages stay local
                  disabled_servers:
                    - "example-server"
                    # Add more server names as needed
                  
                  # Discord name format for messages sent from Discord to game
                  # Options: "username" or "displayname"
                  name_format: "username"
                  
                  # Format for Discord messages sent to game (variables: {name}, {message})
                  message_format: "<gray>[Discord]</gray> <white><bold>{name}</bold>:</white> {message}"
                
                bot-activity:
                  # Valid Types: ONLINE, DO_NOT_DISTURB, IDLE, INVISIBLE
                  status: ONLINE
                  # Valid Types: PLAYING, STREAMING, LISTENING, WATCHING, COMPETING, CUSTOM_STATUS
                  type: CUSTOM_STATUS
                  # Valid placeholders are %online% and/or %max-players% (UPDATES EVERY 5 MINUTES)
                  text: '%online%/%max-players% - play.skyemc.net'
                
                network:
                  # Broadcast join messages to all servers in the network
                  broadcast_join_to_all_servers: true
                  
                  # Broadcast leave messages to all servers in the network
                  broadcast_leave_to_all_servers: true
                  
                  # Show when players transfer between servers
                  show_server_transfers: false
                  
                  # Network join message format (variables: {player}, {luckperms_prefix})
                  join_format: "<green>✅ {luckperms_prefix}<bold>{player}</bold> joined the network!</green>"
                  
                  # Network leave message format (variables: {player}, {luckperms_prefix})
                  leave_format: "<red>❌ {luckperms_prefix}<bold>{player}</bold> left the network!</red>"
                
                messages:
                  # Player join message (variables: {player}, {luckperms_prefix})
                  join: "<green>✅ {luckperms_prefix}<bold>{player}</bold> joined the network!</green>"
                  
                  # Player leave message (variables: {player}, {luckperms_prefix})
                  leave: "<red>❌ {luckperms_prefix}<bold>{player}</bold> left the network!</red>"
                  
                  # Server switch message (variables: {player}, {from}, {to}, {luckperms_prefix})
                  server_switch: "<yellow>🔄 {luckperms_prefix}<bold>{player}</bold> switched from <italic>{from}</italic> to <italic>{to}</italic></yellow>"
                  
                  # Chat message prefix (variables: {server}, {player}, {luckperms_prefix})
                  chat_prefix: "<gray>[<blue>{server}</blue>]</gray> {luckperms_prefix}<white><bold>{player}</bold>:</white> "
                
                # Global Chat message formats (variables: {player}, {luckperms_prefix}, {server})
                global_chat:
                  # Main chat message format when globe icon is shown
                  message_with_icon: "🌐 {luckperms_prefix}<bold>{player}</bold>: {message}"
                  
                  # Main chat message format when globe icon is hidden
                  message_without_icon: "{luckperms_prefix}<bold>{player}</bold>: {message}"
                  
                  # Player joined global chat notification
                  join_message: "🌐 {luckperms_prefix}<bold>{player}</bold> <green>joined global chat</green>"
                  
                  # Player left global chat notification  
                  leave_message: "🌐 {luckperms_prefix}<bold>{player}</bold> <red>left global chat</red>"
                  
                  # New player notification (when they join server with global chat disabled)
                  new_player_notification: "<green>You are not connected to global chat. Type </green><gold><bold>/gc</bold></gold><green> to toggle.</green>"
                """;
            
            FileWriter writer = new FileWriter(configFile);
            writer.write(content);
            writer.close();
            
            logger.info("Created default configuration file: config.yml");
            
        } catch (IOException e) {
            logger.error("Failed to create configuration file: " + e.getMessage());
        }
    }
    
    public void reloadConfig() {
        loadConfig();
        logger.info("Configuration reloaded!");
    }
    
    // Helper methods for safe config access
    private String getString(String path, String defaultValue) {
        return getNestedString(config, path, defaultValue);
    }
    
    private boolean getBoolean(String path, boolean defaultValue) {
        return getNestedBoolean(config, path, defaultValue);
    }
    
    private Object getNestedValue(Map<String, Object> config, String path) {
        String[] keys = path.split("\\.");
        Object current = config;
        
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        
        return current;
    }
    
    private String getNestedString(Map<String, Object> config, String path, String defaultValue) {
        Object value = getNestedValue(config, path);
        return value != null ? value.toString() : defaultValue;
    }
    
    private boolean getNestedBoolean(Map<String, Object> config, String path, boolean defaultValue) {
        Object value = getNestedValue(config, path);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
    
    @SuppressWarnings("unchecked")
    private java.util.List<String> getList(String path, java.util.List<String> defaultValue) {
        Object value = getNestedValue(config, path);
        if (value instanceof java.util.List) {
            return (java.util.List<String>) value;
        }
        return defaultValue;
    }
    
    // Getters
    public String getToken() { return token; }
    public String getChannelId() { return channelId; }
    public boolean isShowPrefixes() { return showPrefixes; }
    public boolean isEnableJoinLeave() { return enableJoinLeave; }
    public boolean isEnableServerSwitch() { return enableServerSwitch; }
    
    // Network configuration getters
    public boolean isBroadcastJoinToAllServers() { return broadcastJoinToAllServers; }
    public boolean isBroadcastLeaveToAllServers() { return broadcastLeaveToAllServers; }
    public boolean isShowServerTransfers() { return showServerTransfers; }
    public String getNetworkJoinFormat() { return networkJoinFormat; }
    public String getNetworkLeaveFormat() { return networkLeaveFormat; }
    
    // Global chat Discord integration getters
    public boolean isOnlyGlobalChatToDiscord() { return onlyGlobalChatToDiscord; }
    public java.util.List<String> getDisabledServers() { return disabledServers; }
    
    // Discord name format getters
    public String getDiscordNameFormat() { return discordNameFormat; }
    public String getDiscordMessageFormat() { return discordMessageFormat; }
    
    public String getJoinMessage() { return joinMessage; }
    public String getLeaveMessage() { return leaveMessage; }
    public String getServerSwitchMessage() { return serverSwitchMessage; }
    public String getChatPrefix() { return chatPrefix; }
    
    // Global chat message format getters
    public String getGlobalChatMessageWithIcon() { return globalChatMessageWithIcon; }
    public String getGlobalChatMessageWithoutIcon() { return globalChatMessageWithoutIcon; }
    public String getGlobalChatJoinMessage() { return globalChatJoinMessage; }
    public String getGlobalChatLeaveMessage() { return globalChatLeaveMessage; }
    public String getGlobalChatNewPlayerNotification() { return globalChatNewPlayerNotification; }
    
    // Bot activity getters
    public String getBotActivityStatus() { return botActivityStatus; }
    public String getBotActivityType() { return botActivityType; }
    public String getBotActivityText() { return botActivityText; }
    
    // Check if Discord is properly configured
    public boolean isConfigured() {
        return !token.equals("YOUR_BOT_TOKEN_HERE") && 
               !channelId.equals("YOUR_CHANNEL_ID_HERE") &&
               !token.equals("REPLACE_WITH_YOUR_ACTUAL_BOT_TOKEN") &&
               !channelId.equals("REPLACE_WITH_YOUR_ACTUAL_CHANNEL_ID") &&
               !token.trim().isEmpty() && 
               !channelId.trim().isEmpty();
    }
}
