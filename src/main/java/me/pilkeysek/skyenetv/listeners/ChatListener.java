package me.pilkeysek.skyenetv.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.utils.GlobalChatManager;
import org.slf4j.Logger;

public class ChatListener {
    
    private final GlobalChatManager globalChatManager;
    private final Logger logger;
    
    public ChatListener(GlobalChatManager globalChatManager, Logger logger) {
        this.globalChatManager = globalChatManager;
        this.logger = logger;
    }
    
    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Process the message through GlobalChatManager
        if (globalChatManager.processPlayerMessage(player, message)) {
            // Message was sent to global chat, cancel the original event
            event.setResult(PlayerChatEvent.ChatResult.denied());
            logger.debug("Player {} sent global message: {}", player.getUsername(), message);
        }
        // If global chat is disabled, let the message pass through normally
    }
}
