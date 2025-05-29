package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.Connection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
        // proxyServer.getServer("lobby");
        Player p = (Player) source;
        // proxyServer.getPlayer(p.getUsername()).get().getCurrentServer();
        Optional<Player> proxiedPlayer = proxyServer.getPlayer(p.getUsername());
        if(Objects.equals(proxiedPlayer.get().getCurrentServer().get().getServerInfo().getName(), "lobby")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You are already connected to the lobby!</red>"));
            return;
        }if(Objects.equals(proxiedPlayer.get().getCurrentServer().get().getServerInfo().getName(), "encaged")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You are ENCAGED >:D</red>"));
            return;
        }
        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Sending you to lobby ...</green>"));
        Optional<RegisteredServer> lobby = proxyServer.getServer("lobby");
        if(lobby.isEmpty()) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error sending you to lobby: Server is not present</red>"));
            return;
        }
        // CompletableFuture<ConnectionRequestBuilder.Result> connectionRequestBuilder = p.createConnectionRequest(lobby.get()).connect();
        p.createConnectionRequest(lobby.get()).connect();

    }
}
