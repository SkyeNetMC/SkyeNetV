package me.pilkeysek.skyenetv.api;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.Collection;

/**
 * SkyeNetV API for cross-server functionality
 * Use this interface to integrate with SkyeNetV features from other plugins
 */
public interface SkyeNetVAPI {
    
    /**
     * Send a cross-server message to a specific player
     * 
     * @param targetPlayer The player to send the message to
     * @param message The message component to send
     * @return true if the message was sent successfully
     */
    boolean sendCrossServerMessage(Player targetPlayer, Component message);
    
    /**
     * Send a cross-server message to a player by name
     * 
     * @param targetPlayerName The name of the player to send the message to
     * @param message The message component to send
     * @return true if the message was sent successfully
     */
    boolean sendCrossServerMessage(String targetPlayerName, Component message);
    
    /**
     * Find a player by name across all servers (supports partial matching)
     * 
     * @param playerName The player name to search for
     * @return Optional containing the player if found
     */
    Optional<Player> findPlayer(String playerName);
    
    /**
     * Get all players across all servers in the network
     * 
     * @return Collection of all online players
     */
    Collection<Player> getAllPlayers();
    
    /**
     * Check if a player is online anywhere in the network
     * 
     * @param playerName The player name to check
     * @return true if the player is online
     */
    boolean isPlayerOnline(String playerName);
    
    /**
     * Get the server name where a player is currently connected
     * 
     * @param player The player to check
     * @return Optional containing the server name if connected
     */
    Optional<String> getPlayerServer(Player player);
    
    /**
     * Broadcast a message to all players on all servers
     * 
     * @param message The message component to broadcast
     */
    void broadcastToNetwork(Component message);
    
    /**
     * Broadcast a message to all players on a specific server
     * 
     * @param serverName The server to broadcast to
     * @param message The message component to broadcast
     */
    void broadcastToServer(String serverName, Component message);
}
