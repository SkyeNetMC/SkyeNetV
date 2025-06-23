// Example backend server plugin message handler for SkyeNetV fly command
// This code should be added to your backend server plugins (Spigot/Paper/etc.)

package com.example.skyenethandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class SkyeNetVHandler extends JavaPlugin implements PluginMessageListener, Listener {

    @Override
    public void onEnable() {
        // Register plugin message channel
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "skyenetv:fly", this);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "skyenetv:teleport", this);
        
        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("SkyeNetV Handler enabled!");
    }

    @Override
    public void onDisable() {
        // Unregister plugin message channels
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "skyenetv:fly");
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "skyenetv:teleport");
        
        getLogger().info("SkyeNetV Handler disabled!");
    }

    @Override
    public void onPluginMessageReceived(String channel, org.bukkit.entity.Player player, byte[] message) {
        if (channel.equals("skyenetv:fly")) {
            handleFlyCommand(message);
        } else if (channel.equals("skyenetv:teleport")) {
            handleTeleportCommand(player, message);
        }
    }

    private void handleFlyCommand(byte[] message) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
            DataInputStream dataStream = new DataInputStream(inputStream);

            String command = dataStream.readUTF();  // Should be "FlyCommand"
            
            if (!command.equals("FlyCommand")) {
                return; // Invalid command
            }

            String playerUUID = dataStream.readUTF();
            String action = dataStream.readUTF();     // "on", "off", or "toggle"
            String senderName = dataStream.readUTF(); // Who sent the command

            // Find the target player
            Player targetPlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));
            if (targetPlayer == null) {
                getLogger().warning("Player with UUID " + playerUUID + " not found for fly command");
                return;
            }

            // Apply flight changes
            boolean currentFlight = targetPlayer.getAllowFlight();
            boolean newFlightState;

            switch (action.toLowerCase()) {
                case "on":
                    newFlightState = true;
                    break;
                case "off":
                    newFlightState = false;
                    break;
                case "toggle":
                default:
                    newFlightState = !currentFlight;
                    break;
            }

            // Set flight ability and flying state
            targetPlayer.setAllowFlight(newFlightState);
            if (newFlightState) {
                targetPlayer.setFlying(true);
            } else {
                targetPlayer.setFlying(false);
            }

            // Send confirmation message to player
            String message_text = newFlightState ? 
                "§a✈ Flight enabled!" : 
                "§e✈ Flight disabled!";
            targetPlayer.sendMessage(message_text);

            // Log the action
            getLogger().info(String.format("Flight %s for %s by %s", 
                newFlightState ? "enabled" : "disabled", 
                targetPlayer.getName(), 
                senderName));

        } catch (IOException e) {
            getLogger().severe("Error processing fly command: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid UUID in fly command: " + e.getMessage());
        }
    }

    private void handleTeleportCommand(Player player, byte[] message) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
            DataInputStream dataStream = new DataInputStream(inputStream);

            String command = dataStream.readUTF();  // Should be "TeleportSpawn"
            
            if (!command.equals("TeleportSpawn")) {
                return; // Invalid command
            }

            double x = dataStream.readDouble();
            double y = dataStream.readDouble();
            double z = dataStream.readDouble();
            float yaw = dataStream.readFloat();
            float pitch = dataStream.readFloat();

            // Teleport player to spawn coordinates
            org.bukkit.Location spawnLocation = new org.bukkit.Location(
                player.getWorld(), x, y, z, yaw, pitch
            );
            
            player.teleport(spawnLocation);
            player.sendMessage("§aTeleported to spawn!");
            
            getLogger().info("Teleported " + player.getName() + " to spawn location");

        } catch (IOException e) {
            getLogger().severe("Error processing teleport command: " + e.getMessage());
        }
    }
}

/* 
 * plugin.yml example:
 * 
 * name: SkyeNetVHandler
 * version: 1.0
 * main: com.example.skyenethandler.SkyeNetVHandler
 * api-version: 1.19
 * description: Handler for SkyeNetV proxy commands
 * 
 */
