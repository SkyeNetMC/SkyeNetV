package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.pilkeysek.skyenetv.config.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Objects;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LobbyCommand implements SimpleCommand {

    private final ProxyServer proxyServer;
    private final Config config;
    
    public LobbyCommand(ProxyServer proxyServer, Config config) {
        this.proxyServer = proxyServer;
        this.config = config;
    }
    
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if(!(source instanceof Player)) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must be a player!</red>"));
            return;
        }
        
        Player player = (Player) source;
        String lobbyServerName = config.getLobbyServerName();
        
        // Check if lobby server exists
        Optional<RegisteredServer> lobby = proxyServer.getServer(lobbyServerName);
        if(lobby.isEmpty()) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error: '" + lobbyServerName + "' server is not registered on this proxy!</red>"));
            source.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Available servers: " + 
                proxyServer.getAllServers().stream()
                    .map(server -> server.getServerInfo().getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None") + "</gold>"));
            return;
        }
        
        // Check if player is already on lobby
        Optional<Player> proxiedPlayer = proxyServer.getPlayer(player.getUsername());
        if(proxiedPlayer.isPresent() && proxiedPlayer.get().getCurrentServer().isPresent()) {
            String currentServer = proxiedPlayer.get().getCurrentServer().get().getServerInfo().getName();
            
            if(Objects.equals(currentServer, lobbyServerName)) {
                // Player is on lobby server, teleport to spawn coordinates
                teleportToSpawn(player);
                return;
            }
            
            if(Objects.equals(currentServer, "encaged")) {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You are ENCAGED >:D</red>"));
                return;
            }
        }
        
        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Sending you to lobby...</green>"));
        player.createConnectionRequest(lobby.get()).connect().thenAccept(result -> {
            if (result.isSuccessful()) {
                // After successfully connecting to lobby, teleport to spawn
                teleportToSpawn(player);
            }
        });
    }
    
    private void teleportToSpawn(Player player) {
        try {
            // Create a plugin message to teleport the player to spawn coordinates
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(outputStream);
            
            // Send teleport data: command, x, y, z, yaw, pitch
            dataStream.writeUTF("TeleportSpawn");
            dataStream.writeDouble(config.getLobbySpawnX());
            dataStream.writeDouble(config.getLobbySpawnY());
            dataStream.writeDouble(config.getLobbySpawnZ());
            dataStream.writeFloat(config.getLobbySpawnYaw());
            dataStream.writeFloat(config.getLobbySpawnPitch());
            
            byte[] data = outputStream.toByteArray();
            
            // Send plugin message to the server
            if (player.getCurrentServer().isPresent()) {
                player.getCurrentServer().get().sendPluginMessage(() -> "skyenetv:teleport", data);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Teleporting to spawn...</green>"));
            }
            
        } catch (IOException e) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Failed to teleport to spawn location!</red>"));
        }
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.lobby");
    }
}
