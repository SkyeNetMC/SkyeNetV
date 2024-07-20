package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.Arrays;

public class SudoCommand implements SimpleCommand {

    private final ProxyServer proxyServer;
    private final Logger logger;

    public SudoCommand(ProxyServer server, Logger logger) {
        this.proxyServer = server;
        this.logger = logger;
    }
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if(!source.hasPermission("skyenetv.command.sudo")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have the required permissions to execute this command!</red>"));
            return;
        }
        proxyServer.getCommandManager().executeAsync(proxyServer.getConsoleCommandSource(), String.join(" ", invocation.arguments()));
        logger.info("A player just executed the following command at the console level: " + String.join(" ", invocation.arguments()));
    }
}
