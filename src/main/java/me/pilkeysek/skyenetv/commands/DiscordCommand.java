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
                source.sendMessage(Component.text("You don't have permission to reload configuration.", NamedTextColor.RED));
                return;
            }
            
            try {
                plugin.reloadDiscordConfig();
                source.sendMessage(Component.text("Configuration reloaded successfully!", NamedTextColor.GREEN));
            } catch (Exception e) {
                source.sendMessage(Component.text("Failed to reload configuration: " + e.getMessage(), NamedTextColor.RED));
            }
        } else if (args[0].equalsIgnoreCase("test")) {
            if (!source.hasPermission("skyenetv.discord.admin")) {
                source.sendMessage(Component.text("You don't have permission to test Discord.", NamedTextColor.RED));
                return;
            }
            
            if (plugin.getDiscordManager() == null) {
                source.sendMessage(Component.text("Discord manager is not initialized!", NamedTextColor.RED));
                return;
            }
            
            try {
                plugin.getDiscordManager().sendTestMessage();
                source.sendMessage(Component.text("Test message sent to Discord!", NamedTextColor.GREEN));
            } catch (Exception e) {
                source.sendMessage(Component.text("Failed to send test message: " + e.getMessage(), NamedTextColor.RED));
            }
        } else if (args[0].equalsIgnoreCase("status")) {
            if (!source.hasPermission("skyenetv.discord.admin")) {
                source.sendMessage(Component.text("You don't have permission to check Discord status.", NamedTextColor.RED));
                return;
            }
            
            if (plugin.getDiscordManager() == null) {
                source.sendMessage(Component.text("Discord manager is not initialized!", NamedTextColor.RED));
                return;
            }
            
            boolean connected = plugin.getDiscordManager().isConnected();
            String channelId = plugin.getDiscordConfig().getChannelId();
            String channelName = plugin.getDiscordManager().getChatChannel() != null ? 
                plugin.getDiscordManager().getChatChannel().getName() : "Unknown";
            
            source.sendMessage(Component.text()
                .append(Component.text("Discord Status:", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text("Connected: ", NamedTextColor.WHITE))
                .append(Component.text(connected ? "✅ Yes" : "❌ No", connected ? NamedTextColor.GREEN : NamedTextColor.RED))
                .append(Component.newline())
                .append(Component.text("Channel: ", NamedTextColor.WHITE))
                .append(Component.text(channelName + " (" + channelId + ")", NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("Global Chat Only: ", NamedTextColor.WHITE))
                .append(Component.text(plugin.getDiscordConfig().isOnlyGlobalChatToDiscord() ? "Yes" : "No", NamedTextColor.YELLOW))
                .build());
        } else if (args[0].equalsIgnoreCase("servers")) {
            if (!source.hasPermission("skyenetv.discord.admin")) {
                source.sendMessage(Component.text("You don't have permission to view server settings.", NamedTextColor.RED));
                return;
            }
            
            java.util.List<String> disabledServers = plugin.getDiscordConfig().getDisabledServers();
            source.sendMessage(Component.text()
                .append(Component.text("Discord Disabled Servers:", NamedTextColor.BLUE))
                .append(Component.newline())
                .append(Component.text(disabledServers.isEmpty() ? "None" : String.join(", ", disabledServers), NamedTextColor.YELLOW))
                .build());
            
            if (plugin.getDiscordManager() == null) {
                source.sendMessage(Component.text("Discord manager is not initialized!", NamedTextColor.RED));
                return;
            }
            
            if (!plugin.getDiscordManager().isConnected()) {
                source.sendMessage(Component.text("Discord bot is not connected!", NamedTextColor.RED));
                return;
            }
            
            plugin.getDiscordManager().sendTestMessage();
            source.sendMessage(Component.text("Test message sent to Discord. Check the Discord channel!", NamedTextColor.GREEN));
        } else if (args[0].equalsIgnoreCase("status")) {
            if (!source.hasPermission("skyenetv.discord.admin")) {
                source.sendMessage(Component.text("You don't have permission to check Discord status.", NamedTextColor.RED));
                return;
            }
            
            if (plugin.getDiscordManager() == null) {
                source.sendMessage(Component.text("Discord manager: NOT INITIALIZED", NamedTextColor.RED));
                return;
            }
            
            boolean connected = plugin.getDiscordManager().isConnected();
            source.sendMessage(Component.text("Discord status: " + (connected ? "CONNECTED" : "DISCONNECTED"), 
                connected ? NamedTextColor.GREEN : NamedTextColor.RED));
                
            if (plugin.getDiscordManager().getChatChannel() != null) {
                source.sendMessage(Component.text("Channel: " + plugin.getDiscordManager().getChatChannel().getName(), NamedTextColor.GRAY));
            } else {
                source.sendMessage(Component.text("Channel: NOT FOUND", NamedTextColor.RED));
            }
        } else {
            source.sendMessage(Component.text("Usage: /discord [reload|test|status]", NamedTextColor.RED));
        }
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length <= 1) {
            if (invocation.source().hasPermission("skyenetv.discord.admin")) {
                return List.of("reload", "test", "status", "servers");
            } else if (invocation.source().hasPermission("skyenetv.discord.reload")) {
                return List.of("reload");
            } else {
                return List.of();
            }
        }
        
        return List.of();
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return true; // Basic command is available to everyone
    }
}
