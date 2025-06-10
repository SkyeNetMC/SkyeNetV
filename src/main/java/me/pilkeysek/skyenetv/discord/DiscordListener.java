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

            // Ignore bot messages first
            if (author.isBot()) {
                return;
            }

            // Check if we have a valid chat channel
            if (discordManager.getChatChannel() == null) {
                plugin.getLogger().error("Discord chat channel is null, cannot process message");
                return;
            }

            // Only handle messages from the configured chat channel - check this early
            if (!message.getChannel().getId().equals(discordManager.getChatChannel().getId())) {
                return; // Silently ignore messages from other channels
            }

            // Now we know this message is from the correct channel, log and process it
            plugin.getLogger().info("Received Discord message from {} in correct channel", author.getName());

            // Get display name from member if available
            String displayName = null;
            if (event.isFromGuild() && event.getMember() != null) {
                displayName = event.getMember().getEffectiveName();
            }

            // Debug logging for message details (only for correct channel)
            plugin.getLogger().info("Discord message details:");
            plugin.getLogger().info("  - author name: '{}'", author.getName());
            plugin.getLogger().info("  - display name: '{}'", displayName);
            plugin.getLogger().info("  - channel ID: '{}'", message.getChannel().getId());
            plugin.getLogger().info("  - expected channel: '{}'", discordManager.getChatChannel().getId());
            plugin.getLogger().info("  - message.getContentRaw(): '{}'", message.getContentRaw());
            plugin.getLogger().info("  - message.getContentDisplay(): '{}'", message.getContentDisplay());
            plugin.getLogger().info("  - message.getContentStripped(): '{}'", message.getContentStripped());
            plugin.getLogger().info("  - message type: {}", message.getType());
            plugin.getLogger().info("  - message flags: {}", message.getFlags());

            // Check for empty content and provide detailed diagnosis
            if (message.getContentRaw().isEmpty()) {
                plugin.getLogger().warn("MESSAGE CONTENT IS EMPTY - Possible causes:");
                plugin.getLogger().warn("  1. Message Content Intent not enabled in Discord Developer Portal");
                plugin.getLogger().warn("  2. Message contains only embeds/attachments (not supported)");
                plugin.getLogger().warn("  3. Message was edited/deleted before processing");
                plugin.getLogger().warn("  4. Bot lacks 'Read Message History' permission in channel");
                plugin.getLogger().warn("  5. Bot was not created with MESSAGE_CONTENT intent enabled in code");
                
                // Don't broadcast empty messages
                return;
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
