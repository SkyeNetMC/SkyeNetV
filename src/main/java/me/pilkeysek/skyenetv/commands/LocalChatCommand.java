package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.utils.PrefixUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class LocalChatCommand implements SimpleCommand {
    private final SkyeNetV plugin;

    public LocalChatCommand(SkyeNetV plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return;
        }

        String[] args = invocation.arguments();
        
        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /lc <message>", NamedTextColor.RED));
            return;
        }

        // Join all arguments to form the message
        String message = String.join(" ", args);

        if (!player.getCurrentServer().isPresent()) {
            player.sendMessage(Component.text("You are not connected to a server!", NamedTextColor.RED));
            return;
        }

        RegisteredServer currentServer = player.getCurrentServer().get().getServer();
        
        // Get formatted player name with prefix
        Component formattedName = PrefixUtils.getFullFormattedName(player);
        
        // Create local chat message format
        Component localChatMessage = Component.text()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("LOCAL", NamedTextColor.YELLOW))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(formattedName)
                .append(Component.text(": ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.WHITE))
                .build();

        // Send to all players on the same server only
        for (Player onlinePlayer : plugin.getServer().getAllPlayers()) {
            if (onlinePlayer.getCurrentServer().isPresent() && 
                onlinePlayer.getCurrentServer().get().getServer().equals(currentServer)) {
                onlinePlayer.sendMessage(localChatMessage);
            }
        }

        plugin.getLogger().info("Local chat message from {} on {}: {}", 
            player.getUsername(), currentServer.getServerInfo().getName(), message);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // Available to all players
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return List.of(); // No suggestions needed for chat command
    }
}
