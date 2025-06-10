package me.pilkeysek.skyenetv.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.pilkeysek.skyenetv.SkyeNetV;
import me.pilkeysek.skyenetv.utils.PrefixUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.*;

public class GlobalChatCommand implements SimpleCommand {
    private final Set<UUID> globalChatPlayers = new HashSet<>();
    private final Map<UUID, GlobalChatSettings> playerSettings = new HashMap<>();
    private final SkyeNetV plugin;

    public static class GlobalChatSettings {
        private boolean enabled = false;
        private boolean showIcon = true;
        private boolean receiveMessages = true;
        private boolean sendMessages = true;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public boolean isShowIcon() { return showIcon; }
        public void setShowIcon(boolean showIcon) { this.showIcon = showIcon; }
        
        public boolean isReceiveMessages() { return receiveMessages; }
        public void setReceiveMessages(boolean receiveMessages) { this.receiveMessages = receiveMessages; }
        
        public boolean isSendMessages() { return sendMessages; }
        public void setSendMessages(boolean sendMessages) { this.sendMessages = sendMessages; }
    }

    public GlobalChatCommand(SkyeNetV plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return;
        }

        String[] args = invocation.arguments();
        UUID playerId = player.getUniqueId();
        
        // Get or create settings for player
        GlobalChatSettings settings = playerSettings.computeIfAbsent(playerId, k -> new GlobalChatSettings());
        
        if (args.length == 0) {
            // Toggle global chat on/off
            settings.setEnabled(!settings.isEnabled());
            if (settings.isEnabled()) {
                globalChatPlayers.add(playerId);
                // Broadcast join message to all players who can receive global messages
                broadcastGlobalChatJoinMessage(player);
            } else {
                globalChatPlayers.remove(playerId);
                // Broadcast leave message to all players who can receive global messages
                broadcastGlobalChatLeaveMessage(player);
            }
            
            String status = settings.isEnabled() ? "enabled" : "disabled";
            NamedTextColor color = settings.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED;
            
            player.sendMessage(Component.text()
                    .append(Component.text("🌐 [GlobalChat] ", NamedTextColor.GOLD))
                    .append(Component.text("Global chat " + status + ".", color))
                    .append(Component.text(" Use ", NamedTextColor.GRAY))
                    .append(Component.text("/gc settings", NamedTextColor.YELLOW)
                            .decoration(TextDecoration.UNDERLINED, true)
                            .clickEvent(ClickEvent.runCommand("/gc settings"))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to open settings"))))
                    .append(Component.text(" for more options.", NamedTextColor.GRAY))
                    .build());
            return;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "settings" -> showSettingsMenu(player, settings);
            case "toggle" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /gc toggle <icon|receive|send>", NamedTextColor.RED));
                    return;
                }
                handleToggle(player, settings, args[1]);
            }
            default -> {
                player.sendMessage(Component.text("Unknown subcommand. Use /gc or /gc settings", NamedTextColor.RED));
            }
        }
    }
    
    private void showSettingsMenu(Player player, GlobalChatSettings settings) {
        player.sendMessage(Component.text()
                .append(Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GOLD, TextDecoration.STRIKETHROUGH))
                .build());
        
        player.sendMessage(Component.text()
                .append(Component.text("🌐 Global Chat Settings", NamedTextColor.GOLD, TextDecoration.BOLD))
                .build());
        
        player.sendMessage(Component.empty());
        
        // Global Chat Status
        Component statusToggle = createToggleComponent("Global Chat", settings.isEnabled(), "/gc");
        player.sendMessage(statusToggle);
        
        // Show Icon Setting
        Component iconToggle = createToggleComponent("Show Globe Icon", settings.isShowIcon(), "/gc toggle icon");
        player.sendMessage(iconToggle);
        
        // Receive Messages Setting
        Component receiveToggle = createToggleComponent("Receive Global Messages", settings.isReceiveMessages(), "/gc toggle receive");
        player.sendMessage(receiveToggle);
        
        // Send Messages Setting
        Component sendToggle = createToggleComponent("Send to Global Chat", settings.isSendMessages(), "/gc toggle send");
        player.sendMessage(sendToggle);
        
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text()
                .append(Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GOLD, TextDecoration.STRIKETHROUGH))
                .build());
    }
    
    private Component createToggleComponent(String settingName, boolean enabled, String command) {
        String status = enabled ? "✓" : "✗";
        NamedTextColor statusColor = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
        String statusText = enabled ? "ON" : "OFF";
        
        return Component.text()
                .append(Component.text(" • ", NamedTextColor.GRAY))
                .append(Component.text(settingName + ": ", NamedTextColor.WHITE))
                .append(Component.text("[" + status + " " + statusText + "]", statusColor, TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand(command))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to toggle"))))
                .build();
    }
    
    private void handleToggle(Player player, GlobalChatSettings settings, String setting) {
        UUID playerId = player.getUniqueId();
        
        switch (setting.toLowerCase()) {
            case "icon" -> {
                settings.setShowIcon(!settings.isShowIcon());
                player.sendMessage(Component.text()
                        .append(Component.text("🌐 ", NamedTextColor.GOLD))
                        .append(Component.text("Globe icon display: ", NamedTextColor.WHITE))
                        .append(Component.text(settings.isShowIcon() ? "ON" : "OFF", 
                                settings.isShowIcon() ? NamedTextColor.GREEN : NamedTextColor.RED))
                        .build());
            }
            case "receive" -> {
                settings.setReceiveMessages(!settings.isReceiveMessages());
                if (!settings.isReceiveMessages()) {
                    globalChatPlayers.remove(playerId);
                } else if (settings.isEnabled()) {
                    globalChatPlayers.add(playerId);
                }
                player.sendMessage(Component.text()
                        .append(Component.text("🌐 ", NamedTextColor.GOLD))
                        .append(Component.text("Receiving global messages: ", NamedTextColor.WHITE))
                        .append(Component.text(settings.isReceiveMessages() ? "ON" : "OFF", 
                                settings.isReceiveMessages() ? NamedTextColor.GREEN : NamedTextColor.RED))
                        .build());
            }
            case "send" -> {
                settings.setSendMessages(!settings.isSendMessages());
                player.sendMessage(Component.text()
                        .append(Component.text("🌐 ", NamedTextColor.GOLD))
                        .append(Component.text("Sending to global chat: ", NamedTextColor.WHITE))
                        .append(Component.text(settings.isSendMessages() ? "ON" : "OFF", 
                                settings.isSendMessages() ? NamedTextColor.GREEN : NamedTextColor.RED))
                        .build());
            }
            default -> {
                player.sendMessage(Component.text("Invalid setting. Use: icon, receive, or send", NamedTextColor.RED));
            }
        }
    }

    public boolean isGlobalChatEnabled(Player player) {
        return globalChatPlayers.contains(player.getUniqueId());
    }
    
    public boolean shouldReceiveGlobalMessages(Player player) {
        GlobalChatSettings settings = playerSettings.get(player.getUniqueId());
        return settings != null && settings.isEnabled() && settings.isReceiveMessages();
    }
    
    public boolean shouldSendGlobalMessages(Player player) {
        GlobalChatSettings settings = playerSettings.get(player.getUniqueId());
        return settings != null && settings.isEnabled() && settings.isSendMessages();
    }
    
    public boolean shouldShowIcon(Player player) {
        GlobalChatSettings settings = playerSettings.get(player.getUniqueId());
        return settings == null || settings.isShowIcon(); // Default to true
    }

    public Set<UUID> getGlobalChatPlayers() {
        return new HashSet<>(globalChatPlayers);
    }
    
    public GlobalChatSettings getPlayerSettings(UUID playerId) {
        return playerSettings.get(playerId);
    }
    
    /**
     * Broadcast a join message when a player enables global chat
     */
    public void broadcastGlobalChatJoinMessage(Player player) {
        // Get the join message format from configuration
        String joinMessageFormat = plugin.getDiscordConfig().getGlobalChatJoinMessage();
        
        // Get the full formatted name with color continuation for prefix
        String playerPrefix = PrefixUtils.getPlayerPrefixText(player);
        
        // Replace placeholders in the message format
        String processedMessage = joinMessageFormat
                .replace("{player}", player.getUsername())
                .replace("{luckperms_prefix}", playerPrefix);
        
        // Parse as MiniMessage
        Component joinMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(processedMessage);
        
        // Send to all players who can receive global messages
        for (Player onlinePlayer : plugin.getServer().getAllPlayers()) {
            if (shouldReceiveGlobalMessages(onlinePlayer)) {
                onlinePlayer.sendMessage(joinMessage);
            }
        }
    }
    
    /**
     * Broadcast a leave message when a player disables global chat
     */
    public void broadcastGlobalChatLeaveMessage(Player player) {
        // Get the leave message format from configuration
        String leaveMessageFormat = plugin.getDiscordConfig().getGlobalChatLeaveMessage();
        
        // Get the player prefix
        String playerPrefix = PrefixUtils.getPlayerPrefixText(player);
        
        // Replace placeholders in the message format
        String processedMessage = leaveMessageFormat
                .replace("{player}", player.getUsername())
                .replace("{luckperms_prefix}", playerPrefix);
        
        // Parse as MiniMessage
        Component leaveMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(processedMessage);
        
        // Send to all players who can receive global messages
        for (Player onlinePlayer : plugin.getServer().getAllPlayers()) {
            if (shouldReceiveGlobalMessages(onlinePlayer)) {
                onlinePlayer.sendMessage(leaveMessage);
            }
        }
    }
    
    /**
     * Send notification to player when they connect with global chat disabled
     */
    public void sendGlobalChatDisabledNotification(Player player) {
        // Get the notification message format from configuration
        String notificationFormat = plugin.getDiscordConfig().getGlobalChatNewPlayerNotification();
        
        // Parse as MiniMessage with click event
        Component notification = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(notificationFormat)
                .clickEvent(ClickEvent.runCommand("/gc"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to toggle global chat")));
        
        player.sendMessage(notification);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        // Allow all players to use global chat
        return true;
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length == 0 || args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String input = args.length == 0 ? "" : args[0].toLowerCase();
            
            if ("settings".startsWith(input)) {
                suggestions.add("settings");
            }
            if ("toggle".startsWith(input)) {
                suggestions.add("toggle");
            }
            
            return suggestions;
        }
        
        if (args.length == 2 && "toggle".equalsIgnoreCase(args[0])) {
            List<String> suggestions = new ArrayList<>();
            String input = args[1].toLowerCase();
            
            if ("icon".startsWith(input)) {
                suggestions.add("icon");
            }
            if ("receive".startsWith(input)) {
                suggestions.add("receive");
            }
            if ("send".startsWith(input)) {
                suggestions.add("send");
            }
            
            return suggestions;
        }
        
        return Collections.emptyList();
    }
}
