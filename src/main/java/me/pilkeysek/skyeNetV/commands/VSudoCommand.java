package me.pilkeysek.skyeNetV.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class VSudoCommand {
    public static final String YOU_ARE_ALREADY_CONSOLE_MESSAGE = "<red>You are already the console ._.</red>";

    public static final String VSUDO_PERMISSION = "skyenetv.command.vsudo";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> vSudoNode = BrigadierCommand.literalArgumentBuilder("vsudo")
                .requires(source -> source.hasPermission(VSUDO_PERMISSION))
                .then(BrigadierCommand.requiredArgumentBuilder("command", StringArgumentType.greedyString())
                        .executes(context -> {
                            CommandSource source = context.getSource();
                            if(source instanceof ConsoleCommandSource) {
                                source.sendMessage(MiniMessage.miniMessage().deserialize(YOU_ARE_ALREADY_CONSOLE_MESSAGE));
                                return 0;
                            }
                            proxy.getCommandManager().executeAsync(proxy.getConsoleCommandSource(), context.getArgument("command", String.class));
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
        return new BrigadierCommand(vSudoNode);
    }
}
