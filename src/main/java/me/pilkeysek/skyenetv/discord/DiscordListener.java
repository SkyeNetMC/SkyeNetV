package me.pilkeysek.skyenetv.discord;

import me.pilkeysek.skyenetv.SkyeNetV;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class DiscordListener extends ListenerAdapter {
    private final SkyeNetV plugin;
    private final DiscordManager discordManager;

    public DiscordListener(SkyeNetV plugin, DiscordManager discordManager) {
        this.plugin = plugin;
        this.discordManager = discordManager;
        plugin.getLogger().info("Discord listener initialized successfully!");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            Message message = event.getMessage();
            User author = event.getAuthor();

            // Debug logging
            plugin.getLogger().info("Received Discord message from {}: {}", author.getName(), message.getContentDisplay());

            // Ignore bot messages
            if (author.isBot()) {
                plugin.getLogger().debug("Ignoring bot message from {}", author.getName());
                return;
            }

            // Check if we have a valid chat channel
            if (discordManager.getChatChannel() == null) {
                plugin.getLogger().error("Discord chat channel is null, cannot process message");
                return;
            }

            // Only handle messages from the configured chat channel
            if (!message.getChannel().getId().equals(discordManager.getChatChannel().getId())) {
                plugin.getLogger().debug("Message not from configured channel. Expected: {}, Got: {}", 
                    discordManager.getChatChannel().getId(), message.getChannel().getId());
                return;
            }

            // Get display name from member if available
            String displayName = null;
            if (event.isFromGuild() && event.getMember() != null) {
                displayName = event.getMember().getEffectiveName();
            }

            plugin.getLogger().info("Broadcasting Discord message to Minecraft players: [{}] {}", 
                displayName != null ? displayName : author.getName(), message.getContentDisplay());

            // Broadcast the message to all players
            discordManager.broadcastDiscordMessage(author.getName(), displayName, message.getContentDisplay());
        } catch (Exception e) {
            plugin.getLogger().error("Error processing Discord message", e);
        }
    }
}
