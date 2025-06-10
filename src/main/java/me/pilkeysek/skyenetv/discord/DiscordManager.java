package me.pilkeysek.skyenetv.discord;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.config.DiscordConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;


import java.awt.Color;

public class DiscordManager {
    private final SkyeNetV plugin;
    private final DiscordConfig discordConfig;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
    
    private JDA jda;
    private TextChannel chatChannel;

    public DiscordManager(SkyeNetV plugin, DiscordConfig discordConfig) {
        this.plugin = plugin;
        this.discordConfig = discordConfig;
            
        initialize();
    }

    private void initialize() {
        try {
            plugin.getLogger().info("Initializing Discord bot with token: {}...", 
                discordConfig.getToken().substring(0, Math.min(10, discordConfig.getToken().length())) + "...");
            
            jda = JDABuilder.createDefault(discordConfig.getToken())
                    .build()
                    .awaitReady();
            
            plugin.getLogger().info("JDA initialized, looking for channel ID: {}", discordConfig.getChannelId());
            
            chatChannel = jda.getTextChannelById(discordConfig.getChannelId());
            if (chatChannel == null) {
                plugin.getLogger().error("Could not find Discord channel with ID: {}", discordConfig.getChannelId());
                plugin.getLogger().error("Available channels:");
                jda.getTextChannels().forEach(channel -> 
                    plugin.getLogger().error("  - {} (ID: {})", channel.getName(), channel.getId()));
                return;
            }
            
            plugin.getLogger().info("Found Discord channel: {} (ID: {})", chatChannel.getName(), chatChannel.getId());
            
            // Register the Discord listener after JDA is ready
            jda.addEventListener(new DiscordListener(plugin, this));
            
            // Send a test message to verify the connection
            sendTestMessage();
            
            plugin.getLogger().info("Discord bot initialized successfully!");
        } catch (Exception e) {
            plugin.getLogger().error("Failed to initialize Discord bot", e);
        }
    }

    public void sendChatMessage(Player player, String message, RegisteredServer server) {
        if (chatChannel == null) return;
        
        String serverName = server.getServerInfo().getName();
        
        // Create the chat prefix using MiniMessage format
        String chatPrefix = discordConfig.getChatPrefix()
                .replace("{server}", serverName)
                .replace("{player}", player.getUsername());
        
        // Convert MiniMessage to plain text for Discord
        Component prefixComponent = miniMessage.deserialize(chatPrefix);
        String prefixDisplay = plainSerializer.serialize(prefixComponent);
        
        String displayMessage = prefixDisplay + message;
        
        chatChannel.sendMessage(displayMessage).queue();
    }

    public void sendServerSwitch(Player player, RegisteredServer from, RegisteredServer to) {
        if (chatChannel == null || !discordConfig.isEnableServerSwitch()) return;

        // Use MiniMessage format from config
        String switchMessage = discordConfig.getServerSwitchMessage()
                .replace("{player}", player.getUsername())
                .replace("{from}", from != null ? from.getServerInfo().getName() : "Unknown")
                .replace("{to}", to.getServerInfo().getName());
        
        // Broadcast to network if enabled
        if (discordConfig.isShowServerTransfers()) {
            broadcastNetworkMessage(switchMessage, "server_switch");
        }
        
        // Convert to plain text for Discord
        Component messageComponent = miniMessage.deserialize(switchMessage);
        String discordMessage = plainSerializer.serialize(messageComponent);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setDescription(discordMessage)
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendPlayerJoin(Player player) {
        if (chatChannel == null || !discordConfig.isEnableJoinLeave()) return;

        // Use network join message format from config if broadcasting to all servers
        String joinMessage;
        if (discordConfig.isBroadcastJoinToAllServers()) {
            joinMessage = discordConfig.getNetworkJoinFormat()
                    .replace("{player}", player.getUsername());
            
            // Broadcast to all servers
            broadcastNetworkMessage(joinMessage, "join");
        } else {
            // Use regular join message
            joinMessage = discordConfig.getJoinMessage()
                    .replace("{player}", player.getUsername());
        }
        
        // Convert to plain text for Discord
        Component messageComponent = miniMessage.deserialize(joinMessage);
        String discordMessage = plainSerializer.serialize(messageComponent);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription(discordMessage)
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendPlayerLeave(Player player) {
        if (chatChannel == null || !discordConfig.isEnableJoinLeave()) return;

        // Use network leave message format from config if broadcasting to all servers
        String leaveMessage;
        if (discordConfig.isBroadcastLeaveToAllServers()) {
            leaveMessage = discordConfig.getNetworkLeaveFormat()
                    .replace("{player}", player.getUsername());
            
            // Broadcast to all servers
            broadcastNetworkMessage(leaveMessage, "leave");
        } else {
            // Use regular leave message
            leaveMessage = discordConfig.getLeaveMessage()
                    .replace("{player}", player.getUsername());
        }
        
        // Convert to plain text for Discord
        Component messageComponent = miniMessage.deserialize(leaveMessage);
        String discordMessage = plainSerializer.serialize(messageComponent);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(discordMessage)
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void broadcastDiscordMessage(String author, String displayName, String message) {
        try {
            // Use the configured name format
            String nameToUse;
            if ("displayname".equals(discordConfig.getDiscordNameFormat())) {
                nameToUse = displayName != null ? displayName : author;
            } else {
                nameToUse = author;
            }
            
            // Use the configured message format
            String formattedMessage = discordConfig.getDiscordMessageFormat()
                    .replace("{name}", nameToUse)
                    .replace("{message}", message);
            
            plugin.getLogger().info("Broadcasting formatted Discord message: {}", formattedMessage);
            
            Component component = miniMessage.deserialize(formattedMessage);

            // Broadcast to all online players
            int playerCount = 0;
            for (Player player : plugin.getServer().getAllPlayers()) {
                player.sendMessage(component);
                playerCount++;
            }
            
            plugin.getLogger().info("Sent Discord message to {} online players", playerCount);
        } catch (Exception e) {
            plugin.getLogger().error("Error broadcasting Discord message to Minecraft", e);
        }
    }
    
    private void broadcastNetworkMessage(String message, String type) {
        Component component = miniMessage.deserialize(message);
        
        plugin.getServer().getAllPlayers().forEach(player -> 
            player.sendMessage(component));
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public JDA getJda() {
        return jda;
    }
    
    public TextChannel getChatChannel() {
        return chatChannel;
    }
    
    public boolean isConnected() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED && chatChannel != null;
    }
    
    public void sendTestMessage() {
        if (chatChannel != null) {
            chatChannel.sendMessage("🔧 **Discord integration test** - SkyeNetV plugin loaded successfully!").queue(
                success -> plugin.getLogger().info("Test message sent to Discord successfully"),
                error -> plugin.getLogger().error("Failed to send test message to Discord: {}", error.getMessage())
            );
        } else {
            plugin.getLogger().error("Cannot send test message - chat channel is null");
        }
    }
}
