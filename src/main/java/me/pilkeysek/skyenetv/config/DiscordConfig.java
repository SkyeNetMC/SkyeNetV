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
    private boolean filterMessages = true;
    private boolean showFilteredHover = true;
    private boolean enableJoinLeave = true;
    private boolean enableServerSwitch = true;
    
    // MiniMessage formats
    private String joinMessage = "<green>✅ <bold>{player}</bold> joined the network!</green>";
    private String leaveMessage = "<red>❌ <bold>{player}</bold> left the network!</red>";
    private String serverSwitchMessage = "<yellow>🔄 <bold>{player}</bold> switched from <italic>{from}</italic> to <italic>{to}</italic></yellow>";
    private String chatPrefix = "<gray>[<blue>{server}</blue>]</gray> <white><bold>{player}</bold>:</white> ";
    private String filteredMessageHover = "<red>Original message: {original}</red>";
    
    public DiscordConfig(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        loadConfig();
    }
    
    private void loadConfig() {
        File configFile = new File(dataDirectory.toFile(), "discord_config.yml");
        
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
            filterMessages = getBoolean("discord.filter_messages", filterMessages);
            showFilteredHover = getBoolean("discord.show_filtered_hover", showFilteredHover);
            enableJoinLeave = getBoolean("discord.enable_join_leave", enableJoinLeave);
            enableServerSwitch = getBoolean("discord.enable_server_switch", enableServerSwitch);
            
            // Load MiniMessage formats
            joinMessage = getString("messages.join", joinMessage);
            leaveMessage = getString("messages.leave", leaveMessage);
            serverSwitchMessage = getString("messages.server_switch", serverSwitchMessage);
            chatPrefix = getString("messages.chat_prefix", chatPrefix);
            filteredMessageHover = getString("messages.filtered_hover", filteredMessageHover);
            
            logger.info("Discord configuration loaded successfully!");
            
        } catch (Exception e) {
            logger.error("Failed to load Discord configuration: " + e.getMessage());
            createDefaultConfig(configFile);
        }
    }
    
    private void createDefaultConfig(File configFile) {
        try {
            String content = """
                # SkyeNetV Discord Configuration
                # Use MiniMessage format for colored text: https://docs.advntr.dev/minimessage/format.html
                
                discord:
                  # Your Discord bot token (get from https://discord.com/developers/applications)
                  token: "YOUR_BOT_TOKEN_HERE"
                  
                  # Discord channel ID where messages will be sent
                  channel: "YOUR_CHANNEL_ID_HERE"
                  
                  # Show server prefixes in chat messages
                  show_prefixes: true
                  
                  # Enable chat message filtering integration
                  filter_messages: true
                  
                  # Show filtered message content on hover
                  show_filtered_hover: true
                  
                  # Enable join/leave notifications
                  enable_join_leave: true
                  
                  # Enable server switch notifications
                  enable_server_switch: true
                
                messages:
                  # Player join message (variables: {player})
                  join: "<green>✅ <bold>{player}</bold> joined the network!</green>"
                  
                  # Player leave message (variables: {player})
                  leave: "<red>❌ <bold>{player}</bold> left the network!</red>"
                  
                  # Server switch message (variables: {player}, {from}, {to})
                  server_switch: "<yellow>🔄 <bold>{player}</bold> switched from <italic>{from}</italic> to <italic>{to}</italic></yellow>"
                  
                  # Chat message prefix (variables: {server}, {player})
                  chat_prefix: "<gray>[<blue>{server}</blue>]</gray> <white><bold>{player}</bold>:</white> "
                  
                  # Hover text for filtered messages (variables: {original})
                  filtered_hover: "<red>Original message: {original}</red>"
                """;
            
            FileWriter writer = new FileWriter(configFile);
            writer.write(content);
            writer.close();
            
            logger.info("Created default Discord configuration file: discord_config.yml");
            
        } catch (IOException e) {
            logger.error("Failed to create Discord configuration file: " + e.getMessage());
        }
    }
    
    public void reloadConfig() {
        loadConfig();
        logger.info("Discord configuration reloaded!");
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
    
    // Getters
    public String getToken() { return token; }
    public String getChannelId() { return channelId; }
    public boolean isShowPrefixes() { return showPrefixes; }
    public boolean isFilterMessages() { return filterMessages; }
    public boolean isShowFilteredHover() { return showFilteredHover; }
    public boolean isEnableJoinLeave() { return enableJoinLeave; }
    public boolean isEnableServerSwitch() { return enableServerSwitch; }
    
    public String getJoinMessage() { return joinMessage; }
    public String getLeaveMessage() { return leaveMessage; }
    public String getServerSwitchMessage() { return serverSwitchMessage; }
    public String getChatPrefix() { return chatPrefix; }
    public String getFilteredMessageHover() { return filteredMessageHover; }
    
    // Check if Discord is properly configured
    public boolean isConfigured() {
        return !token.equals("YOUR_BOT_TOKEN_HERE") && !channelId.equals("YOUR_CHANNEL_ID_HERE");
    }
}
