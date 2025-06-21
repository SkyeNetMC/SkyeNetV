package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.pilkeysek.skyenetv.config.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class DiscordCommand implements SimpleCommand {

    private final Config config;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public DiscordCommand(Config config) {
        this.config = config;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            // Show Discord invite message
            String inviteMessage = config.getDiscordInviteMessage();
            source.sendMessage(miniMessage.deserialize(inviteMessage));
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            // Check permission for reload
            if (!source.hasPermission("skyenetv.discord.reload")) {
                source.sendMessage(miniMessage.deserialize("<red>You don't have permission to reload the configuration!</red>"));
                return;
            }
            // Reload the configuration
            config.reload();
            source.sendMessage(miniMessage.deserialize("<green>Configuration reloaded successfully!</green>"));
        } else {
            source.sendMessage(miniMessage.deserialize("<red>Usage: /discord [reload]</red>"));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.discord");
    }
}
