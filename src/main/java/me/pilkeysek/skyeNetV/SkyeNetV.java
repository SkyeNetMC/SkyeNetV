package me.pilkeysek.skyeNetV;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyeNetV.commands.SkyeNetVCommand;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "skyenetv", name = "SkyeNetV", version = BuildConstants.VERSION, description = "Velocity Plugin for SkyeNet", url = "store.skyenet.co.in", authors = {"PilkeySEK"})
public class SkyeNetV {

    public final ProxyServer proxy;
    public final Logger logger;
    public final Path dataDirectory;

     @Inject
     public SkyeNetV(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
         this.proxy = proxy;
         this.logger = logger;
         this.dataDirectory = dataDirectory;
     }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
         registerCommands();
         logger.info("SkyeNetV version " + BuildConstants.VERSION + " initialized.");
    }

    private void registerCommands() {
        CommandManager manager = proxy.getCommandManager();

        manager.register(
                manager.metaBuilder("skyenetv").plugin(this).build(),
                SkyeNetVCommand.createBrigadierCommand(proxy, this)
        );
    }

    public void fullReload() {

    }
}
