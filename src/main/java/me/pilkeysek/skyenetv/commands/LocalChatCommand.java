package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.utils.GlobalChatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class LocalChatCommand implements SimpleCommand {
    
    private final GlobalChatManager globalChatManager;
    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    
    public LocalChatCommand(SkyeNetV plugin, GlobalChatManager globalChatManager) {
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
        
        // Force disable global chat for this player
        globalChatManager.setGlobalChat(player, false);
        Component message = miniMessage.deserialize(config.getLocalChatEnabledMessage());
        player.sendMessage(message);
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.localchat");
    }
}
