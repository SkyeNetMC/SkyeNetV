package me.pilkeysek.skyenetv.discord;

import me.pilkeysek.skyenetv.SkyeNetV;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordListener extends ListenerAdapter {
    private final SkyeNetV plugin;
    private final DiscordManager discordManager;

    public DiscordListener(SkyeNetV plugin, DiscordManager discordManager) {
        this.plugin = plugin;
        this.discordManager = discordManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        User author = event.getAuthor();

        // Ignore bot messages
        if (author.isBot()) return;

        // Only handle messages from the configured chat channel
        if (!message.getChannel().getId().equals(discordManager.getChatChannel().getId())) return;

        // Broadcast the message to all players
        discordManager.broadcastDiscordMessage(author.getName(), message.getContentDisplay());
    }
}
