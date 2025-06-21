package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.utils.GlobalChatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class GlobalChatCommand implements SimpleCommand {
    
    private final GlobalChatManager globalChatManager;
    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public GlobalChatCommand(SkyeNetV plugin, GlobalChatManager globalChatManager) {
        this.globalChatManager = globalChatManager;
        this.config = plugin.getConfig();
    }
    
    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            Component message = miniMessage.deserialize(config.getPlayerOnlyMessage());
            invocation.source().sendMessage(message);
            return;
        }
        
        Player player = (Player) invocation.source();
        
        if (invocation.arguments().length == 0) {
            // Toggle global chat
            boolean newState = globalChatManager.toggleGlobalChat(player);
            
            String messageKey = newState ? config.getGlobalChatEnabledMessage() : config.getGlobalChatDisabledMessage();
            Component message = miniMessage.deserialize(messageKey);
            player.sendMessage(message);
        } else {
            // Send message to global chat - force it to be global regardless of toggle state
            String message = String.join(" ", invocation.arguments());
            globalChatManager.processPlayerMessage(player, message, true);
        }
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.globalchat");
    }
}
