package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

public class FlyCommand implements SimpleCommand {

    private final ProxyServer proxyServer;
    
    public FlyCommand(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }
    
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        
        if (!(source instanceof Player)) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Only players can use this command!</red>"));
            return;
        }
        
        Player player = (Player) source;
        String[] args = invocation.arguments();
        
        // Handle target player (optional)
        Player targetPlayer = player; // Default to self
        String action = "toggle"; // Default action
        
        if (args.length > 0) {
            // Check if first argument is a player name or action
            Optional<Player> foundPlayer = proxyServer.getPlayer(args[0]);
            if (foundPlayer.isPresent()) {
                // First argument is a player name
                targetPlayer = foundPlayer.get();
                
                // Check permission to modify other players
                if (!player.equals(targetPlayer) && !source.hasPermission("skyenetv.fly.others")) {
                    source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You don't have permission to change other players' fly mode!</red>"));
                    return;
                }
                
                // Check for action argument
                if (args.length > 1) {
                    String actionArg = args[1].toLowerCase();
                    if (actionArg.equals("on") || actionArg.equals("off")) {
                        action = actionArg;
                    }
                }
            } else {
                // First argument is not a player, check if it's an action
                String actionArg = args[0].toLowerCase();
                if (actionArg.equals("on") || actionArg.equals("off")) {
                    action = actionArg;
                } else {
                    source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Player '" + args[0] + "' not found!</red>"));
                    return;
                }
            }
        }
        
        // Send fly command to target player's current server
        sendFlyCommand(targetPlayer, action, source);
    }
    
    private void sendFlyCommand(Player targetPlayer, String action, CommandSource sender) {
        try {
            // Create a plugin message to toggle fly mode
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(outputStream);
            
            // Send fly data: command, player UUID, action, sender name
            dataStream.writeUTF("FlyCommand");
            dataStream.writeUTF(targetPlayer.getUniqueId().toString());
            dataStream.writeUTF(action); // "toggle", "on", or "off"
            dataStream.writeUTF(sender instanceof Player ? ((Player) sender).getUsername() : "Console");
            
            byte[] data = outputStream.toByteArray();
            
            // Send plugin message to the server where target player is
            if (targetPlayer.getCurrentServer().isPresent()) {
                targetPlayer.getCurrentServer().get().sendPluginMessage(() -> "skyenetv:fly", data);
                
                // Send confirmation message
                String targetName = targetPlayer.getUsername();
                if (sender instanceof Player && targetPlayer.equals(sender)) {
                    // Player toggling their own fly
                    switch (action) {
                        case "on":
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Flight enabled!</green>"));
                            break;
                        case "off":
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Flight disabled!</yellow>"));
                            break;
                        default:
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Toggling flight mode...</gray>"));
                            break;
                    }
                } else {
                    // Player/console modifying another player's fly
                    switch (action) {
                        case "on":
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Enabled flight for " + targetName + "!</green>"));
                            break;
                        case "off":
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Disabled flight for " + targetName + "!</yellow>"));
                            break;
                        default:
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Toggling flight mode for " + targetName + "...</gray>"));
                            break;
                    }
                }
            } else {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Target player is not connected to any server!</red>"));
            }
            
        } catch (IOException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Failed to send fly command!</red>"));
        }
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("skyenetv.fly");
    }
}
