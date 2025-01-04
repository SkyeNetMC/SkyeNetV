package me.pilkeysek.skyeNetV;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.pilkeysek.skyeNetV.commands.DiscordCommand;
import me.pilkeysek.skyeNetV.commands.SkyeNetVCommand;
import me.pilkeysek.skyeNetV.commands.StoreCommand;
import me.pilkeysek.skyeNetV.commands.VSudoCommand;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;

@Plugin(id = "skyenetv", name = "SkyeNetV", version = BuildConstants.VERSION, description = "Velocity Plugin for SkyeNet", url = "store.skyenet.co.in", authors = {"PilkeySEK"})
public class SkyeNetV {

    public static File configurationFile;
    public static Config config;
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
        configurationFile = new File((dataDirectory.toFile().getAbsolutePath()) + "/skyenetv.json");
        File dataDirectoryFile = dataDirectory.toFile();
        if(!dataDirectoryFile.exists()) {
            boolean create_res = dataDirectoryFile.mkdir();
            if(!create_res) {
                logger.error("Failed to create data directory {}", dataDirectoryFile.getAbsolutePath());
            }
        }
        if (!configurationFile.exists()) {
            try {
                boolean create_res = configurationFile.createNewFile();
                if(!create_res) {
                    logger.error("Failed to create config file {}", configurationFile.getAbsolutePath());
                }
                Gson gson = new Gson();
                String json = gson.toJson(new Config());
                FileWriter writer = new FileWriter(configurationFile);
                writer.write(json);
                writer.close();

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        if (!(configurationFile.canRead() && configurationFile.canWrite())) {
            logger.error("Cannot read or write to configuration file: {}", configurationFile.getAbsolutePath());
        }
        Gson gson = new GsonBuilder().create();
        try {
            config = gson.fromJson(new FileReader(configurationFile), Config.class);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        registerCommands();
        logger.info("SkyeNetV version " + BuildConstants.VERSION + " initialized.");
    }

    private void registerCommands() {
        CommandManager manager = proxy.getCommandManager();

        manager.register(
                manager.metaBuilder("skyenetv").plugin(this).build(),
                SkyeNetVCommand.createBrigadierCommand(proxy, this)
        );
        manager.register(
                manager.metaBuilder("vsudo").aliases("velocitysudo").plugin(this).build(),
                VSudoCommand.createBrigadierCommand(proxy)
        );
        manager.register(
                manager.metaBuilder("discord").plugin(this).build(),
                DiscordCommand.createBrigadierCommand(proxy)
        );
        manager.register(
                manager.metaBuilder("store").aliases("shop").plugin(this).build(),
                StoreCommand.createBrigadierCommand(proxy)
        );
    }

    public void fullReload() {

    }
}
