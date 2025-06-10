package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.pilkeysek.skyenetv.SkyeNetV;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public class DiscordCommand implements SimpleCommand {
    private final SkyeNetV plugin;
    
    public DiscordCommand(SkyeNetV plugin) {
        this.plugin = plugin;
    }
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        
        if (args.length == 0) {
            // Show Discord server invite
            final Component component = MiniMessage.miniMessage().deserialize(
                "<click:open_url:'https://discord.gg/wUEKv8AzgE'>" +
                "<dark_green>ᴊᴏɪɴ ᴛʜᴇ</dark_green> <light_purple>ꜱᴋʏᴇɴᴇᴛ</light_purple> " +
                "<dark_green>ᴄᴏᴍᴍᴜɴɪᴛʏ ᴏɴ ᴏᴜʀ</dark_green> <blue>ᴅɪꜱᴄᴏʀᴅ</blue>" +
                "</click>"
            );
            source.sendMessage(component);
            return;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            if (!source.hasPermission("skyenetv.discord.reload")) {
                source.sendMessage(Component.text("You don't have permission to reload Discord configuration.", NamedTextColor.RED));
                return;
            }
            
            try {
                plugin.reloadDiscordConfig();
                source.sendMessage(Component.text("Discord configuration reloaded successfully!", NamedTextColor.GREEN));
            } catch (Exception e) {
                source.sendMessage(Component.text("Failed to reload Discord configuration: " + e.getMessage(), NamedTextColor.RED));
            }
        } else {
            source.sendMessage(Component.text("Unknown subcommand. Use /discord for help.", NamedTextColor.RED));
        }
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length <= 1) {
            if (invocation.source().hasPermission("skyenetv.discord.reload")) {
                return List.of("reload");
            }
        }
        
        return List.of();
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // Basic command is available to everyone
    }
}
