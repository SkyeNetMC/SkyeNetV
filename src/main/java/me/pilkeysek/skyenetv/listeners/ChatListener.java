package me.pilkeysek.skyenetv.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.utils.ChatManager;
import org.slf4j.Logger;

public class ChatListener {
    
    private final ChatManager chatManager;
    private final Logger logger;
    
    public ChatListener(ChatManager chatManager, Logger logger) {
        this.chatManager = chatManager;
        this.logger = logger;
    }
    
    @Subscribe(order = PostOrder.LAST)
    public void onPlayerChat(PlayerChatEvent event) {
        // Check if event is already cancelled by another plugin
        if (!event.getResult().isAllowed()) {
            return; // Don't process if already cancelled
        }
        
        try {
            Player player = event.getPlayer();
            String message = event.getMessage();
            
            // Process message through chat manager for cross-server forwarding
            chatManager.processPlayerMessage(player, message);
            
            logger.info("Chat message forwarded by SkyeNetV for {}, backend handles local chat", player.getUsername());
        } catch (Exception e) {
            logger.error("Error processing chat message in SkyeNetV", e);
        }
    }
}
