package me.pilkeysek.skyenetv.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerLeaveListener {

    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public PlayerLeaveListener(Config config) {
        this.config = config;
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();

        // Retrieve the network leave message
        String leaveMessage = config.getLeaveMessage("network");
        if (leaveMessage != null && !leaveMessage.isEmpty()) {
            Component message = miniMessage.deserialize(leaveMessage.replace("{player}", player.getUsername()));
            player.sendMessage(message);
        }
    }
}