package me.pilkeysek.skyeNetV.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyeNetV.SkyeNetV;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SkyeNetVCommand {

    private static final String VERSION_MESSAGE = "<aqua>SkyeNetV</aqua><br><gold>Version: </gold><dark_aqua>2.0</dark_aqua>";
    private static final String NO_SUBCOMMAND_PERMISSION_MESSAGE = "<gray>You do not have permission to execute any subcommand.</gray>";
    private static final String START_RELOADING_MESSAGE = "<aqua>Starting reload...</aqua>";
    private static final String RELOAD_SUCCESS_MESSAGE = "<aqua>Successfully reloaded!</aqua>";

    public static final String RELOAD_PERMISSION = "skyenetv.command.skyenetv.reload";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy, SkyeNetV plugin) {
        LiteralCommandNode<CommandSource> skyeNetVNode = BrigadierCommand.literalArgumentBuilder("skyenetv")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    String fullMessage = VERSION_MESSAGE;
                    boolean hasSubCommandPermission = false;
                    if(source.hasPermission(RELOAD_PERMISSION)) hasSubCommandPermission = true;
                    if(!hasSubCommandPermission) {
                        fullMessage += "<br>" + NO_SUBCOMMAND_PERMISSION_MESSAGE;
                    }
                    source.sendMessage(MiniMessage.miniMessage().deserialize(fullMessage));
                    return Command.SINGLE_SUCCESS;
                })
                .then(BrigadierCommand.literalArgumentBuilder("version")
                        .executes(context -> {
                            CommandSource source = context.getSource();
                            source.sendMessage(MiniMessage.miniMessage().deserialize(VERSION_MESSAGE));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(BrigadierCommand.literalArgumentBuilder("reload")
                        .requires(source -> source.hasPermission(RELOAD_PERMISSION))
                        .executes(context -> {
                            CommandSource source = context.getSource();
                            source.sendMessage(MiniMessage.miniMessage().deserialize(START_RELOADING_MESSAGE));
                            plugin.fullReload();
                            source.sendMessage(MiniMessage.miniMessage().deserialize(RELOAD_SUCCESS_MESSAGE));
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
        return new BrigadierCommand(skyeNetVNode);
    }
}
