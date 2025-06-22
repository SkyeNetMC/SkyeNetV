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
        } else {
            source.sendMessage(miniMessage.deserialize("<red>Usage: /discord</red>"));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.discord");
    }
}
