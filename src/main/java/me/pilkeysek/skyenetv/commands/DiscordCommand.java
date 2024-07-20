package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class DiscordCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        /* final TextComponent component = Component.text("Join the ")
                .color(
                        TextColor.color(0, 255, 255)
                )
                .append(
                        Component.text("SkyeNet").
                                color(TextColor.color(255, 215, 0))
                ).append(
                        Component.text(" Discord Server: ")
                                .color(TextColor.color(0, 255, 255)
                                )
                ).append(
                        Component.text("discord.gg/wUEKv8AzgE")
                                .clickEvent(ClickEvent.clickEvent(
                                        ClickEvent.Action.OPEN_URL, "https://discord.gg/wUEKv8AzgE")
                                ).color(TextColor.color(0, 0, 255))
                );*/
        final Component component = MiniMessage.miniMessage().deserialize("<click:open_url:'https://discord.gg/wUEKv8AzgE'><dark_green>ᴊᴏɪɴ ᴛʜᴇ</dark_green> <light_purple>ꜱᴋʏᴇɴᴇᴛ</light_purple> <dark_green>ᴄᴏᴍᴍᴜɴɪᴛʏ ᴏɴ ᴏᴜʀ</dark_green> <blue>ᴅɪꜱᴄᴏʀᴅ</blue></click>");
        source.sendMessage(component);
    }
}
