package me.pilkeysek.skyenetv.discord;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.pilkeysek.skyenetv.SkyeNetV;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.awt.Color;

public class DiscordManager {
    private final SkyeNetV plugin;
    private JDA jda;
    private TextChannel chatChannel;
    private String token;
    private String channelId;

    public DiscordManager(SkyeNetV plugin, String token, String channelId) {
        this.plugin = plugin;
        this.token = token;
        this.channelId = channelId;
        initialize();
    }

    private void initialize() {
        try {
            jda = JDABuilder.createDefault(token)
                    .build()
                    .awaitReady();
            
            chatChannel = jda.getTextChannelById(channelId);
            if (chatChannel == null) {
                plugin.getLogger().error("Could not find Discord channel with ID: " + channelId);
                return;
            }
        } catch (Exception e) {
            plugin.getLogger().error("Failed to initialize Discord bot", e);
        }
    }

    public void sendChatMessage(Player player, String message, RegisteredServer server) {
        if (chatChannel == null) return;
        
        String serverName = server.getServerInfo().getName();
        String format = String.format("**%s** `%s` » %s", serverName, player.getUsername(), message);
        chatChannel.sendMessage(format).queue();
    }

    public void sendServerSwitch(Player player, RegisteredServer from, RegisteredServer to) {
        if (chatChannel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setDescription(String.format("**%s** switched from `%s` to `%s`",
                        player.getUsername(),
                        from != null ? from.getServerInfo().getName() : "null",
                        to.getServerInfo().getName()))
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendPlayerJoin(Player player) {
        if (chatChannel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription(String.format("**%s** joined the network", player.getUsername()))
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendPlayerLeave(Player player) {
        if (chatChannel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(String.format("**%s** left the network", player.getUsername()))
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
