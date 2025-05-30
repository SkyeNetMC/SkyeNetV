package me.pilkeysek.skyenetv.discord;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.config.DiscordConfig;
import me.pilkeysek.skyenetv.modules.ChatFilterModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;

import java.awt.Color;

public class DiscordManager {
    private final SkyeNetV plugin;
    private final ChatFilterModule chatFilter;
    private final DiscordConfig discordConfig;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
    
    private JDA jda;
    private TextChannel chatChannel;

    public DiscordManager(SkyeNetV plugin, DiscordConfig discordConfig) {
        this.plugin = plugin;
        this.discordConfig = discordConfig;
        this.chatFilter = plugin.getChatFilterModule();
            
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
        
        String displayMessage;
        if (discordConfig.isFilterMessages()) {
            // Apply chat filter to the message
            ChatFilterModule.FilterResult filterResult = chatFilter.filterMessage(message, player.getUsername());
            
            // Get player prefix from LuckPerms if enabled
            String prefix = discordConfig.isShowPrefixes() ? getPlayerPrefix(player) : "";
            String serverName = server.getServerInfo().getName();
            
            // Create the chat prefix using MiniMessage format
            String chatPrefix = discordConfig.getChatPrefix()
                    .replace("{server}", serverName)
                    .replace("{player}", player.getUsername());
            
            // Convert MiniMessage to plain text for Discord
            Component prefixComponent = miniMessage.deserialize(chatPrefix);
            String prefixDisplay = plainSerializer.serialize(prefixComponent);
            
            if (filterResult.wasFiltered()) {
                // Show filtered message with reason in Discord
                displayMessage = String.format("%s [Filtered: %s]", 
                        prefixDisplay, filterResult.getFilterReason());
                
                if (discordConfig.isShowFilteredHover()) {
                    // Add original message info
                    displayMessage += String.format(" (Original: %s)", filterResult.getOriginalMessage());
                }
            } else {
                // Show normal message
                displayMessage = prefixDisplay + filterResult.getFilteredMessage();
            }
        } else {
            // No filtering, show message as-is
            String prefix = discordConfig.isShowPrefixes() ? getPlayerPrefix(player) : "";
            String serverName = server.getServerInfo().getName();
            
            // Create the chat prefix using MiniMessage format
            String chatPrefix = discordConfig.getChatPrefix()
                    .replace("{server}", serverName)
                    .replace("{player}", player.getUsername());
            
            // Convert MiniMessage to plain text for Discord
            Component prefixComponent = miniMessage.deserialize(chatPrefix);
            String prefixDisplay = plainSerializer.serialize(prefixComponent);
            
            displayMessage = prefixDisplay + message;
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
        Component component;
        
        if (discordConfig.isFilterMessages()) {
            // Apply chat filter to Discord message
            ChatFilterModule.FilterResult filterResult = chatFilter.filterMessage(message, author);
            
            if (filterResult.wasFiltered() && discordConfig.isShowFilteredHover()) {
                // Show filtered message with hover revealing original content
                String hoverText = discordConfig.getFilteredMessageHover()
                        .replace("{original}", filterResult.getOriginalMessage());
                
                Component hoverComponent = miniMessage.deserialize(hoverText);
                Component filteredComponent = miniMessage.deserialize("<red>[Filtered]</red>")
                        .hoverEvent(HoverEvent.showText(hoverComponent));
                
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
