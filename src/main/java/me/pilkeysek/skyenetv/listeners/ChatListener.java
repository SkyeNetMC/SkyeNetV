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
        
        // Always process through GlobalChatManager (handles both global and local chat with proper formatting)
        globalChatManager.processPlayerMessage(player, message);
        
        // Always cancel the original event to prevent backend server duplication
        event.setResult(PlayerChatEvent.ChatResult.denied());
        logger.info("Chat message processed by SkyeNet for {}, backend event cancelled", player.getUsername());
    }
}
