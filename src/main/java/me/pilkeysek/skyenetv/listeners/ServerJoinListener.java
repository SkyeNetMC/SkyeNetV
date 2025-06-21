package me.pilkeysek.skyenetv.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ServerJoinListener {

    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ServerJoinListener(Config config) {
        this.config = config;
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String serverName = event.getServer().getServerInfo().getName();

        // Retrieve the join message for the server
        String joinMessage = config.getServerJoinMessage(serverName);
        if (joinMessage != null && !joinMessage.isEmpty()) {
            Component message = miniMessage.deserialize(joinMessage.replace("{player}", player.getUsername()));
            player.sendMessage(message);
        }
    }
}