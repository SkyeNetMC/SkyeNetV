package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyenetv.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrivateMessageCommand implements SimpleCommand {

    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final Map<UUID, UUID> lastMessageSender = new HashMap<>();
    private final ProxyServer server;

    public PrivateMessageCommand(Config config, ProxyServer server) {
        this.config = config;
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            source.sendMessage(miniMessage.deserialize(config.getPlayerOnlyMessage()));
            return;
        }

        Player sender = (Player) source;
        String[] args = invocation.arguments();

        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize("<red>Usage: /msg <player> <message></red>"));
            return;
        }

        String targetName = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Player target = server.getPlayer(targetName).orElse(null);
        if (target == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Player not found!</red>"));
            return;
        }

        Component privateMessage = miniMessage.deserialize(config.getPrivateMessageFormat())
                .replaceText(builder -> builder.match("{sender}").replacement(sender.getUsername()))
                .replaceText(builder -> builder.match("{message}").replacement(message));

        target.sendMessage(privateMessage);
        sender.sendMessage(miniMessage.deserialize(config.getPrivateMessageSentFormat())
                .replaceText(builder -> builder.match("{message}").replacement(message)));

        lastMessageSender.put(target.getUniqueId(), sender.getUniqueId());
    }

    public void reply(Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source instanceof Player)) {
            source.sendMessage(miniMessage.deserialize(config.getPlayerOnlyMessage()));
            return;
        }

        Player sender = (Player) source;
        UUID lastSenderUUID = lastMessageSender.get(sender.getUniqueId());

        if (lastSenderUUID == null) {
            sender.sendMessage(miniMessage.deserialize("<red>No one to reply to!</red>"));
            return;
        }

        Player target = server.getPlayer(lastSenderUUID).orElse(null);
        if (target == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Player not found!</red>"));
            return;
        }

        String message = String.join(" ", invocation.arguments());

        Component privateMessage = miniMessage.deserialize("<gray>[PM]</gray> <white>{sender}</white>: {message}")
                .replaceText(builder -> builder.match("{sender}").replacement(sender.getUsername()))
                .replaceText(builder -> builder.match("{message}").replacement(message));

        target.sendMessage(privateMessage);
        sender.sendMessage(miniMessage.deserialize("<gray>[PM]</gray> <white>You</white>: {message}")
                .replaceText(builder -> builder.match("{message}").replacement(message)));

        lastMessageSender.put(target.getUniqueId(), sender.getUniqueId());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.msg");
    }
}
