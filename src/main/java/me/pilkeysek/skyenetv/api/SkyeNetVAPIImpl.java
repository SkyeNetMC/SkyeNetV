package me.pilkeysek.skyenetv.api;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * Implementation of the SkyeNetV API
 * Provides cross-server functionality for other plugins
 */
public class SkyeNetVAPIImpl implements SkyeNetVAPI {
    
    private final ProxyServer server;
    
    public SkyeNetVAPIImpl(ProxyServer server) {
        this.server = server;
    }
    
    @Override
    public boolean sendCrossServerMessage(Player targetPlayer, Component message) {
        try {
            targetPlayer.sendMessage(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean sendCrossServerMessage(String targetPlayerName, Component message) {
        Optional<Player> player = findPlayer(targetPlayerName);
        if (player.isPresent()) {
            return sendCrossServerMessage(player.get(), message);
        }
        return false;
    }
    
    @Override
    public Optional<Player> findPlayer(String playerName) {
        // First try exact match (case-insensitive)
        for (Player player : server.getAllPlayers()) {
            if (player.getUsername().equalsIgnoreCase(playerName)) {
                return Optional.of(player);
            }
        }
        
        // Then try partial match (case-insensitive)
        for (Player player : server.getAllPlayers()) {
            if (player.getUsername().toLowerCase().startsWith(playerName.toLowerCase())) {
                return Optional.of(player);
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public Collection<Player> getAllPlayers() {
        return server.getAllPlayers();
    }
    
    @Override
    public boolean isPlayerOnline(String playerName) {
        return findPlayer(playerName).isPresent();
    }
    
    @Override
    public Optional<String> getPlayerServer(Player player) {
        return player.getCurrentServer()
                .map(serverConnection -> serverConnection.getServerInfo().getName());
    }
    
    @Override
    public void broadcastToNetwork(Component message) {
        for (Player player : server.getAllPlayers()) {
            player.sendMessage(message);
        }
    }
    
    @Override
    public void broadcastToServer(String serverName, Component message) {
        for (Player player : server.getAllPlayers()) {
            Optional<String> playerServer = getPlayerServer(player);
            if (playerServer.isPresent() && playerServer.get().equalsIgnoreCase(serverName)) {
                player.sendMessage(message);
            }
        }
    }
}
