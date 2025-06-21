package me.pilkeysek.skyenetv.discord;

import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyenetv.config.DiscordConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

public class DiscordManager {
    
    private final ProxyServer server;
    private final Logger logger;
    private final DiscordConfig config;
    private JDA jda;
    private TextChannel chatChannel;
    private DiscordListener discordListener;
    private boolean connected = false;
    
    public DiscordManager(ProxyServer server, Logger logger, DiscordConfig config) {
        this.server = server;
        this.logger = logger;
        this.config = config;
    }
    
    public CompletableFuture<Boolean> initialize() {
        if (!config.isEnabled()) {
            logger.info("Discord integration is disabled in config");
            return CompletableFuture.completedFuture(false);
        }
        
        String token = config.getBotToken();
        if (token == null || token.isEmpty()) {
            logger.error("Discord bot token is not configured!");
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Initializing Discord bot...");
                
                jda = JDABuilder.createDefault(token)
                        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                        .build();
                
                jda.awaitReady();
                
                // Get the chat channel
                String channelId = config.getChatChannelId();
                if (channelId != null && !channelId.isEmpty()) {
                    chatChannel = jda.getTextChannelById(channelId);
                    if (chatChannel == null) {
                        logger.error("Could not find Discord channel with ID: {}", channelId);
                        return false;
                    }
                    
                    // Add Discord listener if discord-to-game is enabled
                    if (config.isDiscordToGameEnabled()) {
                        discordListener = new DiscordListener(server, logger, channelId);
                        jda.addEventListener(discordListener);
                        logger.info("Discord-to-game messaging enabled");
                    }
                }
                
                connected = true;
                logger.info("Discord bot successfully connected!");
                return true;
                
            } catch (Exception e) {
                logger.error("Failed to initialize Discord bot", e);
                connected = false;
                return false;
            }
        });
    }
    
    public void shutdown() {
        if (jda != null) {
            logger.info("Shutting down Discord bot...");
            jda.shutdown();
            connected = false;
        }
    }
    
    public boolean isConnected() {
        return connected && jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }
     public void sendToDiscord(String message) {
        if (!isConnected() || chatChannel == null) {
            return;
        }

        try {
            chatChannel.sendMessage(message).queue(
                success -> logger.debug("Message sent to Discord: {}", message),
                error -> logger.error("Failed to send message to Discord", error)
            );
        } catch (Exception e) {
            logger.error("Error sending message to Discord", e);
        }
    }
    
    public void sendToDiscord(String playerName, String message) {
        if (!isConnected() || chatChannel == null) {
            return;
        }

        try {
            String formattedMessage = String.format("**%s**: %s", playerName, message);
            chatChannel.sendMessage(formattedMessage).queue(
                success -> logger.debug("Message sent to Discord: {}", formattedMessage),
                error -> logger.error("Failed to send message to Discord", error)
            );
        } catch (Exception e) {
            logger.error("Error sending message to Discord", e);
        }
    }
    
    public JDA getJDA() {
        return jda;
    }
    
    public TextChannel getChatChannel() {
        return chatChannel;
    }
}
