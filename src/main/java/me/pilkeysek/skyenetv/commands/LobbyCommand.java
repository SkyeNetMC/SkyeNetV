package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Objects;
import java.util.Optional;

public class LobbyCommand implements SimpleCommand {

    private final ProxyServer proxyServer;
    public LobbyCommand(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if(!(source instanceof Player)) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You must be a player!</red>"));
            return;
        }
        
        Player p = (Player) source;
        
        // Check if lobby server exists
        Optional<RegisteredServer> lobby = proxyServer.getServer("lobby");
        if(lobby.isEmpty()) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error: 'lobby' server is not registered on this proxy!</red>"));
            source.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Available servers: " + 
                proxyServer.getAllServers().stream()
                    .map(server -> server.getServerInfo().getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("None") + "</gold>"));
            return;
        }
        
        // Check if player is already on lobby
        Optional<Player> proxiedPlayer = proxyServer.getPlayer(p.getUsername());
        if(proxiedPlayer.isPresent() && proxiedPlayer.get().getCurrentServer().isPresent()) {
            String currentServer = proxiedPlayer.get().getCurrentServer().get().getServerInfo().getName();
            
            if(Objects.equals(currentServer, "lobby")) {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You are already connected to the lobby!</red>"));
                return;
            }
            
            if(Objects.equals(currentServer, "encaged")) {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You are ENCAGED >:D</red>"));
                return;
            }
        }
        
        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Sending you to lobby ...</green>"));
        p.createConnectionRequest(lobby.get()).connect();
    }
}
