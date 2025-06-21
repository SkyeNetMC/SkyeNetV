package me.pilkeysek.skyenetv.discord;

import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.slf4j.Logger;

public class DiscordListener extends ListenerAdapter {
    
    private final ProxyServer server;
    private final Logger logger;
    private final String chatChannelId;
    
    public DiscordListener(ProxyServer server, Logger logger, String chatChannelId) {
        this.server = server;
        this.logger = logger;
        this.chatChannelId = chatChannelId;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot()) {
            return;
        }
        
        // Only process messages from the configured chat channel
        if (!event.getChannel().getId().equals(chatChannelId)) {
            return;
        }
        
        Message message = event.getMessage();
        Member member = event.getMember();
        
        if (member == null) {
            return;
        }
        
        String content = message.getContentDisplay();
        if (content.trim().isEmpty()) {
            return;
        }
        
        // Format the Discord message for in-game
        String displayName = member.getEffectiveName();
        Component discordMessage = Component.text()
                .append(Component.text("💬 ", TextColor.fromHexString("#5865F2"))) // Discord blue
                .append(Component.text("[Discord] ", NamedTextColor.BLUE))
                .append(Component.text(displayName + ": ", NamedTextColor.WHITE))
                .append(Component.text(content, NamedTextColor.GRAY))
                .build();
        
        // Send to all online players
        server.getAllPlayers().forEach(player -> player.sendMessage(discordMessage));
        
        logger.info("[Discord -> Game] {}: {}", displayName, content);
    }
}
