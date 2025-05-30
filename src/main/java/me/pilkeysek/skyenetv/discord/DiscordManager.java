package me.pilkeysek.skyenetv.discord;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.modules.ChatFilterModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;

import java.awt.Color;

public class DiscordManager {
    private final SkyeNetV plugin;
    private final ChatFilterModule chatFilter;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    // Configuration options
    private final boolean showPrefixes;
    private final boolean filterMessages;
    private final boolean showFilteredHover;
    private final boolean enableJoinLeave;
    private final boolean enableServerSwitch;
    // ...existing fields...
    private JDA jda;
    private TextChannel chatChannel;
    private String token;
    private String channelId;

    public DiscordManager(SkyeNetV plugin, String token, String channelId) {
        this.plugin = plugin;
        this.token = token;
        this.channelId = channelId;
        this.chatFilter = plugin.getChatFilterModule();
        
        // Load configuration options from plugin config
        this.showPrefixes = true; // Will implement config reading later
        this.filterMessages = true;
        this.showFilteredHover = true;
        this.enableJoinLeave = true;
        this.enableServerSwitch = true;
            
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
        
        String displayMessage;
        if (filterMessages) {
            // Apply chat filter to the message
            ChatFilterModule.FilterResult filterResult = chatFilter.filterMessage(message, player.getUsername());
            
            // Get player prefix from LuckPerms if enabled
            String prefix = showPrefixes ? getPlayerPrefix(player) : "";
            String prefixDisplay = prefix.isEmpty() ? "" : prefix + " ";
            
            String serverName = server.getServerInfo().getName();
            
            if (filterResult.wasFiltered()) {
                // Show filtered message with reason in Discord
                displayMessage = String.format("**%s** %s`%s` » [Filtered: %s]", 
                        serverName, prefixDisplay, player.getUsername(), filterResult.getFilterReason());
            } else {
                // Show normal message
                displayMessage = String.format("**%s** %s`%s` » %s", 
                        serverName, prefixDisplay, player.getUsername(), filterResult.getFilteredMessage());
            }
        } else {
            // No filtering, show message as-is
            String prefix = showPrefixes ? getPlayerPrefix(player) : "";
            String prefixDisplay = prefix.isEmpty() ? "" : prefix + " ";
            String serverName = server.getServerInfo().getName();
            displayMessage = String.format("**%s** %s`%s` » %s", 
                    serverName, prefixDisplay, player.getUsername(), message);
        }
        
        chatChannel.sendMessage(displayMessage).queue();
    }
    
    private String getPlayerPrefix(Player player) {
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
            String prefix = metaData.getPrefix();
            return prefix != null ? prefix : "";
        } catch (Exception e) {
            plugin.getLogger().warn("Failed to get LuckPerms prefix for " + player.getUsername() + ": " + e.getMessage());
            return "";
        }
    }

    public void sendServerSwitch(Player player, RegisteredServer from, RegisteredServer to) {
        if (chatChannel == null || !enableServerSwitch) return;

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
        if (chatChannel == null || !enableJoinLeave) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription(String.format("**%s** joined the network", player.getUsername()))
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void sendPlayerLeave(Player player) {
        if (chatChannel == null || !enableJoinLeave) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(String.format("**%s** left the network", player.getUsername()))
                .setTimestamp(java.time.Instant.now());

        chatChannel.sendMessageEmbeds(embed.build()).queue();
    }

    public void broadcastDiscordMessage(String author, String message) {
        Component component;
        
        if (filterMessages) {
            // Apply chat filter to Discord message
            ChatFilterModule.FilterResult filterResult = chatFilter.filterMessage(message, author);
            
            if (filterResult.wasFiltered() && showFilteredHover) {
                // Show filtered message with hover revealing original content
                Component filteredComponent = miniMessage.deserialize("<red>[Filtered]</red>")
                        .hoverEvent(HoverEvent.showText(Component.text()
                                .append(Component.text("Original message: " + filterResult.getOriginalMessage(), NamedTextColor.GRAY))
                                .append(Component.newline())
                                .append(Component.text("Filtered for: " + filterResult.getFilterReason(), NamedTextColor.RED))
                                .build()));
                
                component = Component.text()
                        .append(Component.text("Discord", NamedTextColor.BLUE))
                        .append(Component.text(" " + author + " » ", NamedTextColor.GRAY))
                        .append(filteredComponent)
                        .build();
            } else if (filterResult.wasFiltered()) {
                // Show filtered message without hover
                component = Component.text()
                        .append(Component.text("Discord", NamedTextColor.BLUE))
                        .append(Component.text(" " + author + " » ", NamedTextColor.GRAY))
                        .append(Component.text("[Filtered]", NamedTextColor.RED))
                        .build();
            } else {
                // Show normal message
                component = Component.text()
                        .append(Component.text("Discord", NamedTextColor.BLUE))
                        .append(Component.text(" " + author + " » ", NamedTextColor.GRAY))
                        .append(Component.text(filterResult.getFilteredMessage(), NamedTextColor.WHITE))
                        .build();
            }
        } else {
            // No filtering, show message as-is
            component = Component.text()
                    .append(Component.text("Discord", NamedTextColor.BLUE))
                    .append(Component.text(" " + author + " » ", NamedTextColor.GRAY))
                    .append(Component.text(message, NamedTextColor.WHITE))
                    .build();
        }

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
