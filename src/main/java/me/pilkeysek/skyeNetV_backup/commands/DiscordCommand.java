package me.pilkeysek.skyeNetV.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyeNetV.SkyeNetV;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class DiscordCommand {
    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> discordNode = BrigadierCommand.literalArgumentBuilder("discord")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    String msg = SkyeNetV.config.discord_command_message;
                    source.sendMessage(MiniMessage.miniMessage().deserialize(msg));
                    return Command.SINGLE_SUCCESS;
                }).build();
        return new BrigadierCommand(discordNode);
    }
}
