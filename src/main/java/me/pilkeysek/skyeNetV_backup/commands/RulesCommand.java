package me.pilkeysek.skyeNetV.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.pilkeysek.skyeNetV.config.RulesConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RulesCommand implements SimpleCommand {

    private final RulesConfig rulesConfig;

    public RulesCommand(RulesConfig rulesConfig) {
        this.rulesConfig = rulesConfig;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        // Handle subcommands
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "add":
                    handleAddRule(source, args);
                    break;
                case "remove":
                    handleRemoveRule(source, args);
                    break;
                case "edit":
                    handleEditRule(source, args);
                    break;
                case "header":
                    handleSetHeader(source, args);
                    break;
                case "footer":
                    handleSetFooter(source, args);
                    break;
                case "reload":
                    handleReload(source);
                    break;
                default:
                    showRules(source);
                    break;
            }
        } else {
            // Just show the rules if no arguments
            showRules(source);
        }
    }

    private void showRules(CommandSource source) {
        // Send header
        source.sendMessage(MiniMessage.miniMessage().deserialize(rulesConfig.getHeader()));
        
        // Send each rule
        for (RulesConfig.Rule rule : rulesConfig.getRules()) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(
                    rule.getId() + ". " + rule.getTitle()));
            source.sendMessage(MiniMessage.miniMessage().deserialize("   " + rule.getDescription()));
        }
        
        // Send footer
        source.sendMessage(MiniMessage.miniMessage().deserialize(rulesConfig.getFooter()));
    }

    private void handleAddRule(CommandSource source, String[] args) {
        if (!source.hasPermission("skyenetv.rules.admin")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to add rules!</red>"));
            return;
        }

        if (args.length < 3) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /rules add \"<title>\" \"<description>\"</red>"));
            return;
        }

        // Parse arguments considering quoted strings
        List<String> parsedArgs = parseQuotedArgs(Arrays.copyOfRange(args, 1, args.length));
        if (parsedArgs.size() < 2) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /rules add \"<title>\" \"<description>\"</red>"));
            return;
        }

        String title = parsedArgs.get(0);
        String description = parsedArgs.get(1);
        
        rulesConfig.addRule(new RulesConfig.Rule(0, title, description));
        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rule added successfully!</green>"));
    }

    private void handleRemoveRule(CommandSource source, String[] args) {
        if (!source.hasPermission("skyenetv.rules.admin")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to remove rules!</red>"));
            return;
        }

        if (args.length < 2) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /rules remove <id></red>"));
            return;
        }

        try {
            int id = Integer.parseInt(args[1]);
            boolean removed = rulesConfig.removeRule(id);
            if (removed) {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rule removed successfully!</green>"));
            } else {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Rule with ID " + id + " not found!</red>"));
            }
        } catch (NumberFormatException e) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid rule ID. Must be a number.</red>"));
        }
    }

    private void handleEditRule(CommandSource source, String[] args) {
        if (!source.hasPermission("skyenetv.rules.admin")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to edit rules!</red>"));
            return;
        }

        if (args.length < 3) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /rules edit <id> \"<title>\" \"<description>\"</red>"));
            return;
        }

        try {
            int id = Integer.parseInt(args[1]);
            
            // Parse arguments considering quoted strings
            List<String> parsedArgs = parseQuotedArgs(Arrays.copyOfRange(args, 2, args.length));
            if (parsedArgs.size() < 2) {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /rules edit <id> \"<title>\" \"<description>\"</red>"));
                return;
            }

            String title = parsedArgs.get(0);
            String description = parsedArgs.get(1);
            
            boolean updated = rulesConfig.updateRule(id, title, description);
            if (updated) {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rule updated successfully!</green>"));
            } else {
                source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Rule with ID " + id + " not found!</red>"));
            }
        } catch (NumberFormatException e) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Invalid rule ID. Must be a number.</red>"));
        }
    }

    private void handleSetHeader(CommandSource source, String[] args) {
        if (!source.hasPermission("skyenetv.rules.admin")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to edit rules!</red>"));
            return;
        }

        if (args.length < 2) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /rules header \"<new header>\"</red>"));
            return;
        }

        // Join the rest of the arguments to form the header
        String header = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        rulesConfig.setHeader(header);
        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rules header updated successfully!</green>"));
    }

    private void handleSetFooter(CommandSource source, String[] args) {
        if (!source.hasPermission("skyenetv.rules.admin")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to edit rules!</red>"));
            return;
        }

        if (args.length < 2) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>Usage: /rules footer \"<new footer>\"</red>"));
            return;
        }

        // Join the rest of the arguments to form the footer
        String footer = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        rulesConfig.setFooter(footer);
        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rules footer updated successfully!</green>"));
    }

    private void handleReload(CommandSource source) {
        if (!source.hasPermission("skyenetv.rules.admin")) {
            source.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have permission to reload rules!</red>"));
            return;
        }

        rulesConfig.loadConfig();
        source.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rules reloaded successfully!</green>"));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            if (invocation.source().hasPermission("skyenetv.rules.admin")) {
                suggestions.add("add");
                suggestions.add("remove");
                suggestions.add("edit");
                suggestions.add("header");
                suggestions.add("footer");
                suggestions.add("reload");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit")) {
                for (RulesConfig.Rule rule : rulesConfig.getRules()) {
                    suggestions.add(String.valueOf(rule.getId()));
                }
            }
        }

        return suggestions;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(suggest(invocation));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        // Allow anyone to view rules, but require permission for admin commands
        if (invocation.arguments().length > 0) {
            String subcommand = invocation.arguments()[0].toLowerCase();
            if (subcommand.equals("add") || 
                subcommand.equals("remove") || 
                subcommand.equals("edit") || 
                subcommand.equals("header") || 
                subcommand.equals("footer") || 
                subcommand.equals("reload")) {
                return invocation.source().hasPermission("skyenetv.rules.admin");
            }
        }
        return true;
    }
    
    // Utility method to parse arguments considering quotes
    private List<String> parseQuotedArgs(String[] args) {
        List<String> result = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean insideQuotes = false;
        
        for (String arg : args) {
            if (arg.startsWith("\"") && !insideQuotes) {
                insideQuotes = true;
                currentArg.append(arg.substring(1));
            } else if (arg.endsWith("\"") && insideQuotes) {
                insideQuotes = false;
                currentArg.append(" ").append(arg, 0, arg.length() - 1);
                result.add(currentArg.toString());
                currentArg = new StringBuilder();
            } else if (insideQuotes) {
                currentArg.append(" ").append(arg);
            } else {
                result.add(arg);
            }
        }
        
        // If we're still inside quotes at the end, add what we have
        if (insideQuotes && currentArg.length() > 0) {
            result.add(currentArg.toString());
        }
        
        return result;
    }
}
