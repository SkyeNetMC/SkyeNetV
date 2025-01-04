package me.pilkeysek.skyeNetV.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyeNetV.SkyeNetV;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class StoreCommand {
    public static final String STORE_MESSAGE = "<aqua>Store: <click:open_url:'" + SkyeNetV.config.store_url + "'><dark_aqua>" + SkyeNetV.config.store_url + "</dark_aqua></click>";

    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> storeNode = BrigadierCommand.literalArgumentBuilder("store")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(MiniMessage.miniMessage().deserialize(STORE_MESSAGE));
                    return Command.SINGLE_SUCCESS;
                }).build();
        return new BrigadierCommand(storeNode);
    }
}
