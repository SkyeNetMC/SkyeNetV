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
import net.kyori.adventure.text.format.NamedTextColor;
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
            jda = JDABuilder.createDefault(discordConfig.getToken())
                    .build()
                    .awaitReady();
            
            chatChannel = jda.getTextChannelById(discordConfig.getChannelId());
            if (chatChannel == null) {
                plugin.getLogger().error("Could not find Discord channel with ID: " + discordConfig.getChannelId());
                return;
            }
            
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

        // Use MiniMessage format from config
        String joinMessage = discordConfig.getJoinMessage()
                .replace("{player}", player.getUsername());
        
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

        // Use MiniMessage format from config
        String leaveMessage = discordConfig.getLeaveMessage()
                .replace("{player}", player.getUsername());
        
        // Convert to plain text for Discord
        Component messageComponent = miniMessage.deserialize(leaveMessage);
        String discordMessage = plainSerializer.serialize(messageComponent);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(discordMessage)
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void broadcastDiscordMessage(String author, String message) {
        Component component = Component.text()
                .append(Component.text("Discord", NamedTextColor.BLUE))
                .append(Component.text(" " + author + " » ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.WHITE))
                .build();

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
}
