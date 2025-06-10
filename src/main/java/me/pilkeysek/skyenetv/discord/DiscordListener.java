package me.pilkeysek.skyenetv.discord;

import me.pilkeysek.skyenetv.SkyeNetV;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class DiscordListener extends ListenerAdapter {
    private final DiscordManager discordManager;

    public DiscordListener(SkyeNetV plugin, DiscordManager discordManager) {
        this.discordManager = discordManager;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        Message message = event.getMessage();
        User author = event.getAuthor();

        // Ignore bot messages
        if (author.isBot()) return;

        // Only handle messages from the configured chat channel
        if (!message.getChannel().getId().equals(discordManager.getChatChannel().getId())) return;

        // Get display name from member if available
        String displayName = null;
        if (event.isFromGuild() && event.getMember() != null) {
            displayName = event.getMember().getEffectiveName();
        }

        // Broadcast the message to all players
        discordManager.broadcastDiscordMessage(author.getName(), displayName, message.getContentDisplay());
    }
}
