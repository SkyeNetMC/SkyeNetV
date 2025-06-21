package me.pilkeysek.skyenetv.config;

import org.slf4j.Logger;

public class DiscordConfig {
    
    private final Logger logger;
    private final Config mainConfig;
    
    public DiscordConfig(Config mainConfig, Logger logger) {
        this.logger = logger;
        this.mainConfig = mainConfig;
        if (mainConfig == null) {
            throw new IllegalArgumentException("Main config cannot be null");
        }
        logger.info("Discord configuration using main config.yml");
    }
    
    // Fallback constructor for backward compatibility
    @Deprecated
    public DiscordConfig(java.nio.file.Path dataDirectory, Logger logger) {
        throw new UnsupportedOperationException("Discord configuration now uses main config.yml. Use DiscordConfig(Config, Logger) constructor.");
    }
    
    public boolean isEnabled() {
        return mainConfig.isDiscordEnabled();
    }
    
    public String getBotToken() {
        String token = mainConfig.getDiscordBotToken();
        return (token != null && !token.isEmpty() && !"YOUR_ACTUAL_BOT_TOKEN_HERE".equals(token)) ? token : null;
    }
    
    public String getChatChannelId() {
        String channelId = mainConfig.getDiscordChatChannelId();
        return (channelId != null && !channelId.isEmpty() && !"YOUR_ACTUAL_CHANNEL_ID_HERE".equals(channelId)) ? channelId : null;
    }
    
    public boolean isGlobalChatToDiscordEnabled() {
        return mainConfig.isGlobalChatToDiscordEnabled();
    }
    
    public boolean isDiscordToGameEnabled() {
        return mainConfig.isDiscordToGameEnabled();
    }
    
    public boolean isJoinLeaveToDiscordEnabled() {
        return mainConfig.isJoinLeaveToDiscordEnabled();
    }
    
    public String getGameToDiscordFormat() {
        return mainConfig.getGameToDiscordFormat();
    }
    
    public String getDiscordToGameFormat() {
        return mainConfig.getDiscordToGameFormat();
    }
    
    public void reload() {
        mainConfig.reload();
        logger.info("Discord configuration reloaded from main config.yml");
    }
}
