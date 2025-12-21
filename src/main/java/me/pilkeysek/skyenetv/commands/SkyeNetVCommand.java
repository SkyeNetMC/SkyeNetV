package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.config.Config;
import me.pilkeysek.skyenetv.discord.DiscordManager;
import me.pilkeysek.skyenetv.config.ServerGroupsRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SkyeNetVCommand implements SimpleCommand {

    private final Config config;
    private final DiscordManager discordManager;
    private final ServerGroupsRegistry serverGroupsRegistry;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public SkyeNetVCommand(SkyeNetV plugin, Config config, DiscordManager discordManager, ServerGroupsRegistry serverGroupsRegistry) {
        this.config = config;
        this.discordManager = discordManager;
        this.serverGroupsRegistry = serverGroupsRegistry;
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
        boolean configSuccess = config.reload();
        boolean groupsSuccess = true;

        // Reload server groups if available
        if (serverGroupsRegistry != null) {
            groupsSuccess = serverGroupsRegistry.reload();
        }

        if (configSuccess) {
            // Reinitialize Discord connection with new config
            if (discordManager != null) {
                discordManager.shutdown();
                discordManager.initialize();
            }
        }

        if (configSuccess && groupsSuccess) {
            String reloadSuccess = config.getString("messages.reload_success", "<green>✅ SkyeNetV configuration reloaded successfully!</green>");
            source.sendMessage(miniMessage.deserialize(reloadSuccess));
        } else if (configSuccess) {
            // Config reloaded but server groups reload failed
            String partial = config.getString("messages.reload_partial", "<yellow>⚠️ Configuration reloaded, but server_groups.yml failed to reload. Check console.</yellow>");
            source.sendMessage(miniMessage.deserialize(partial));
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
