package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.utils.PrefixUtils;
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

        // Search for player by exact name first, then by partial name
        Player target = findPlayer(targetName);
        if (target == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Player '" + targetName + "' not found!</red>"));
            return;
        }
        
        if (target.equals(sender)) {
            sender.sendMessage(miniMessage.deserialize("<red>Did you forget to take your meds again? You can't message yourself!</red>"));
            return;
        }

        Component privateMessage = miniMessage.deserialize(config.getPrivateMessageFormat())
                .replaceText(builder -> builder.match("{sender}").replacement(PrefixUtils.getFullFormattedName(sender).toString()))
                .replaceText(builder -> builder.match("{message}").replacement(message));

        target.sendMessage(privateMessage);
        sender.sendMessage(miniMessage.deserialize(config.getPrivateMessageSentFormat())
                .replaceText(builder -> builder.match("{sender}").replacement(PrefixUtils.getFullFormattedName(target).toString()))
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

        // Find player by UUID across all servers
        Player target = null;
        for (Player player : server.getAllPlayers()) {
            if (player.getUniqueId().equals(lastSenderUUID)) {
                target = player;
                break;
            }
        }
        
        if (target == null) {
            sender.sendMessage(miniMessage.deserialize("<red>Player is no longer online!</red>"));
            return;
        }
        
        final Player finalTarget = target; // Make final for lambda usage

        String message = String.join(" ", invocation.arguments());

        Component privateMessage = miniMessage.deserialize(config.getPrivateMessageFormat())
                .replaceText(builder -> builder.match("{sender}").replacement(PrefixUtils.getFullFormattedName(sender).toString()))
                .replaceText(builder -> builder.match("{message}").replacement(message));

        finalTarget.sendMessage(privateMessage);
        sender.sendMessage(miniMessage.deserialize(config.getPrivateMessageSentFormat())
                .replaceText(builder -> builder.match("{sender}").replacement(PrefixUtils.getFullFormattedName(finalTarget).toString()))
                .replaceText(builder -> builder.match("{message}").replacement(message)));

        lastMessageSender.put(finalTarget.getUniqueId(), sender.getUniqueId());
    }

    /**
     * Find a player by name across all servers
     * First tries exact match, then partial match (case-insensitive)
     */
    private Player findPlayer(String name) {
        // First try exact match (case-insensitive)
        for (Player player : server.getAllPlayers()) {
            if (player.getUsername().equalsIgnoreCase(name)) {
                return player;
            }
        }
        
        // Then try partial match (case-insensitive)
        for (Player player : server.getAllPlayers()) {
            if (player.getUsername().toLowerCase().startsWith(name.toLowerCase())) {
                return player;
            }
        }
        
        return null;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.msg");
    }
}
