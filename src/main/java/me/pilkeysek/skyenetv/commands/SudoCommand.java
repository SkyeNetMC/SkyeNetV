package me.pilkeysek.skyenetv.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SudoCommand {

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxyServer) {
        LiteralCommandNode<CommandSource> sudoCommandNode = LiteralArgumentBuilder.<CommandSource>literal("vsudo")
                .requires(source -> source.hasPermission("skyenetv.command.sudo"))
                // .then(RequiredArgumentBuilder.argument("command", StringArgumentType.string()))
                .executes(context -> {
                    context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>Please provide an argument!</red>"));
                    return 0;
                })
                .then(BrigadierCommand.requiredArgumentBuilder("command", StringArgumentType.greedyString())
                        // Here the logic of the command "/test <some argument>" is executed
                        .executes(context -> {
                            if(context.getSource() instanceof ConsoleCommandSource) {
                                context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>You are already the console ._.</red>"));
                                return 0;
                            }

                            String args = context.getArgument("command", String.class);
                            context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Executing command: </green><aqua>" + args + "</aqua>"));
                            proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), args);
                            return Command.SINGLE_SUCCESS;
                        })
                )
                /*.then(RequiredArgumentBuilder.argument("command", StringArgumentType.greedyString()))
                .executes(context -> {
                    if(context.getSource() instanceof ConsoleCommandSource) {
                        context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<red>You are already the console ._.</red>"));
                        return 0;
                    }

                    String args = context.getArgument("command", String.class);
                    context.getSource().sendMessage(MiniMessage.miniMessage().deserialize("<green>Executing command: </green><aqua>" + args + "</aqua>"));
                    proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), args);
                    return Command.SINGLE_SUCCESS;
                })*/
                .build();
        return new BrigadierCommand(sudoCommandNode);
    }
}
