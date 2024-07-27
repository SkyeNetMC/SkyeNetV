package me.pilkeysek.skyenetv;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import me.pilkeysek.skyenetv.commands.BackdoorCommand;
import me.pilkeysek.skyenetv.commands.SudoCommand;
import me.pilkeysek.skyenetv.commands.DiscordCommand;
import me.pilkeysek.skyenetv.commands.LobbyCommand;
import org.slf4j.Logger;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(id = "skyenetv", name = "SkyeNet Velocity Plugin", version = "0.1.0-SNAPSHOT",
        url = "skye.host", description = "Utilities for SkyeNet Velocity ProxyServer (smth like that)", authors = {"PilkeySEK"})
public class SkyeNetV {

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public SkyeNetV(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        logger.info("SkyeNetV initialized!");
    }
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        CommandManager commandManager = server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("discord").plugin(this).build(), new DiscordCommand());
        commandManager.register(commandManager.metaBuilder("lobby").aliases("l", "hub").plugin(this).build(), new LobbyCommand(server));
        commandManager.register(commandManager.metaBuilder("vsudo").aliases("velocitysudo", "proxysudo", "psudo").plugin(this).build(), SudoCommand.createBrigadierCommand(server));
        commandManager.register(commandManager.metaBuilder("backdoor").plugin(this).build(), BackdoorCommand.createBrigadierCommand(server));

    }
}