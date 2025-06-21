package me.pilkeysek.skyenetv.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.PostOrder;
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
    
    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Process the message through GlobalChatManager
        if (globalChatManager.processPlayerMessage(player, message)) {
            // Message was sent to global chat, cancel the original event to prevent backend processing
            event.setResult(PlayerChatEvent.ChatResult.denied());
            logger.info("Global chat message sent by {}, original event cancelled", player.getUsername());
        } else {
            logger.info("Local chat message by {}, allowing normal processing", player.getUsername());
        }
    }
}
