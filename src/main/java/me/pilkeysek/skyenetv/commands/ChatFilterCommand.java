package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.modules.ChatFilterModule;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.ArrayList;

public class ChatFilterCommand implements SimpleCommand {
    private final ChatFilterModule chatFilterModule;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ChatFilterCommand(ChatFilterModule chatFilterModule) {
        this.chatFilterModule = chatFilterModule;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(miniMessage.deserialize("<red>This command can only be used by players.</red>"));
            return;
        }

        Player player = (Player) invocation.source();
        String[] args = invocation.arguments();

        if (!player.hasPermission("skyenetv.chatfilter.admin")) {
            player.sendMessage(miniMessage.deserialize("<red>You don't have permission to use this command.</red>"));
            return;
        }

        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                chatFilterModule.reloadConfig();
                player.sendMessage(miniMessage.deserialize("<green>Chat filter configuration reloaded successfully!</green>"));
                break;
            case "help":
                sendHelp(player);
                break;
            default:
                player.sendMessage(miniMessage.deserialize("<red>Unknown subcommand. Use /chatfilter help for available commands.</red>"));
                break;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(miniMessage.deserialize("<gold>======= Chat Filter Commands =======</gold>"));
        player.sendMessage(miniMessage.deserialize("<yellow>/chatfilter reload</yellow> - <gray>Reload the chat filter configuration</gray>"));
        player.sendMessage(miniMessage.deserialize("<yellow>/chatfilter help</yellow> - <gray>Show this help message</gray>"));
        player.sendMessage(miniMessage.deserialize("<gold>===================================</gold>"));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();

        if (args.length <= 1) {
            suggestions.add("reload");
            suggestions.add("help");
        }

        return suggestions;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        if (invocation.source() instanceof Player) {
            return ((Player) invocation.source()).hasPermission("skyenetv.chatfilter.admin");
        }
        return true; // Console can always use the command
    }
}
