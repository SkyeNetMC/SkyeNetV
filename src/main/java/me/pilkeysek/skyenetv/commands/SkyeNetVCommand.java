package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SkyeNetVCommand implements SimpleCommand {

    private final Config config;
    private final DiscordManager discordManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public SkyeNetVCommand(SkyeNetV plugin, Config config, DiscordManager discordManager) {
        this.config = config;
        this.discordManager = discordManager;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            // Show plugin info
            showPluginInfo(source);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            // Reload configuration
            reloadConfiguration(source);
        } else {
            source.sendMessage(miniMessage.deserialize("<red>Usage: /skyenetv reload</red>"));
        }
    }
    
    private void showPluginInfo(CommandSource source) {
        source.sendMessage(miniMessage.deserialize("<gold>===== SkyeNetV Plugin =====</gold>"));
        source.sendMessage(miniMessage.deserialize("<yellow>Version:</yellow> <white>3.2.1</white>"));
        source.sendMessage(miniMessage.deserialize("<yellow>Author:</yellow> <white>SkyeNetwork Team</white>"));
        source.sendMessage(miniMessage.deserialize("<yellow>Commands:</yellow>"));
        source.sendMessage(miniMessage.deserialize("<white>/skyenetv reload</white> <gray>- Reload the configuration</gray>"));
        source.sendMessage(miniMessage.deserialize("<gold>=======================</gold>"));
    }
    
    private void reloadConfiguration(CommandSource source) {
        // Check permission
        if (!source.hasPermission("skyenetv.admin")) {
            source.sendMessage(miniMessage.deserialize("<red>You don't have permission to reload the configuration!</red>"));
            return;
        }
        
        // Reload the configuration
        boolean success = config.reload();
        
        if (success) {
            // Reinitialize Discord connection with new config
            if (discordManager != null) {
                discordManager.shutdown();
                discordManager.initialize();
            }
            
            // Get reload success message from config or use default if not found
            String reloadSuccess = config.getString("messages.reload_success", "<green>✅ SkyeNetV configuration reloaded successfully!</green>");
            source.sendMessage(miniMessage.deserialize(reloadSuccess));
        } else {
            String reloadFailure = config.getString("messages.reload_failure", "<red>❌ Failed to reload configuration. Check console for errors.</red>");
            source.sendMessage(miniMessage.deserialize(reloadFailure));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        // Anyone can run /skyenetv for info, but reload requires permissions checked in the execute method
        return true;
    }
}
