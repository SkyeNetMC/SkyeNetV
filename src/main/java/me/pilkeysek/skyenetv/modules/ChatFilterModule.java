package me.pilkeysek.skyenetv.modules;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ChatFilterModule {
    private final Logger logger;
    private final Path dataDirectory;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private Map<String, Object> regexConfig;
    private Map<String, Object> wordlistConfig;
    private Map<String, Object> mainConfig;
    private final List<Pattern> spamPatterns = new ArrayList<>();
    private final List<Pattern> ipPatterns = new ArrayList<>(); 
    private final List<Pattern> capsPatterns = new ArrayList<>();
    private final List<Pattern> customPatterns = new ArrayList<>();
    private final List<String> blockedWords = new ArrayList<>();
    private final String configFolder = "chatfilter";
    private String prefix;
    private boolean enabled = true;

    public ChatFilterModule(ProxyServer server, Logger logger, Path dataDirectory) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        createDefaultConfigs();
        loadConfigs();
    }

    public void reloadConfig() {
        createDefaultConfigs();
        loadConfigs();
        logger.info("Chat filter configuration reloaded.");
    }

    private void createDefaultConfigs() {
        // Create chatfilter folder directly in plugin folder
        File folder = new File(dataDirectory.toFile(), configFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Create regex.yml if it doesn't exist
        File regexFile = new File(folder, "regex.yml");
        if (!regexFile.exists()) {
            try {
                createDefaultRegexConfig(regexFile);
                logger.info("Created default chat filter regex configuration.");
            } catch (Exception e) {
                logger.warn("Failed to create regex.yml: " + e.getMessage());
            }
        }

        // Create wordlist.yml if it doesn't exist
        File wordlistFile = new File(folder, "wordlist.yml");
        if (!wordlistFile.exists()) {
            try {
                createDefaultWordlistConfig(wordlistFile);
                logger.info("Created default chat filter wordlist configuration.");
            } catch (Exception e) {
                logger.warn("Failed to create wordlist.yml: " + e.getMessage());
            }
        }

        // Create main config if it doesn't exist
        File mainConfigFile = new File(dataDirectory.toFile(), "chatfilter-config.yml");
        if (!mainConfigFile.exists()) {
            try {
                createDefaultMainConfig(mainConfigFile);
                logger.info("Created default chat filter main configuration.");
            } catch (Exception e) {
                logger.warn("Failed to create chatfilter-config.yml: " + e.getMessage());
            }
        }
    }

    private void createDefaultRegexConfig(File file) throws IOException {
        Map<String, Object> config = new HashMap<>();
        config.put("bypass-permission", "skyenetv.chatfilter.bypass");
        config.put("Enable-regex", true);
        config.put("blocked-message", "<prefix>Your message was filtered by pattern: <pattern>");

        // IP blocking config
        Map<String, Object> ipConfig = new HashMap<>();
        ipConfig.put("enabled", true);
        ipConfig.put("regex", "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
        ipConfig.put("advanced-regex", "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
        config.put("block-ips", ipConfig);

        // Spam config
        Map<String, Object> spamConfig = new HashMap<>();
        spamConfig.put("enabled", true);
        spamConfig.put("threshold", 4);
        spamConfig.put("regex", "(.)\\1{3,}");
        spamConfig.put("whitelist", List.of("....", "????"));
        config.put("block-spam-chars", spamConfig);

        // Caps config
        Map<String, Object> capsConfig = new HashMap<>();
        capsConfig.put("enabled", true);
        capsConfig.put("threshold", 60);
        capsConfig.put("min-length", 6);
        config.put("block-caps", capsConfig);

        // Custom patterns config
        Map<String, Object> customConfig = new HashMap<>();
        customConfig.put("enabled", false);
        Map<String, Object> patterns = new HashMap<>();
        patterns.put("test-pattern", "\\b(badword\\d+)\\b");
        customConfig.put("patterns", patterns);
        config.put("custom-patterns", customConfig);

        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter(file);
        yaml.dump(config, writer);
        writer.close();
    }

    private void createDefaultWordlistConfig(File file) throws IOException {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", true);
        config.put("bypass-permission", "skyenetv.wordlist.bypass");
        config.put("blocked-message", "<prefix>Your message was filtered for containing a blocked word: <word>");
        config.put("list", List.of("badword", "example", "test"));

        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter(file);
        yaml.dump(config, writer);
        writer.close();
    }

    private void createDefaultMainConfig(File file) throws IOException {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", true);
        config.put("prefix", "<dark_red>[ChatFilter]</dark_red> ");
        config.put("replacement", "<red>[Filtered]</red>");
        config.put("debug", false);
        config.put("blocked-words", List.of());

        Map<String, Object> modules = new HashMap<>();
        Map<String, Object> chatFilter = new HashMap<>();
        chatFilter.put("enabled", true);
        
        Map<String, Object> wordlist = new HashMap<>();
        wordlist.put("enabled", true);
        chatFilter.put("wordlist", wordlist);
        
        Map<String, Object> regex = new HashMap<>();
        regex.put("enabled", true);
        chatFilter.put("regex", regex);
        
        modules.put("ChatFilter", chatFilter);
        config.put("modules", modules);

        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter(file);
        yaml.dump(config, writer);
        writer.close();
    }

    private void loadConfigs() {
        // First clear all existing data
        spamPatterns.clear();
        ipPatterns.clear();
        capsPatterns.clear();
        customPatterns.clear();
        blockedWords.clear();
        
        logger.info("Loading chat filter configurations...");
        
        Yaml yaml = new Yaml();
        
        try {
            // Load regex config
            File regexFile = new File(dataDirectory.toFile(), configFolder + "/regex.yml");
            if (regexFile.exists()) {
                FileInputStream fis = new FileInputStream(regexFile);
                regexConfig = yaml.load(fis);
                fis.close();
            }
            
            // Load wordlist config
            File wordlistFile = new File(dataDirectory.toFile(), configFolder + "/wordlist.yml");
            if (wordlistFile.exists()) {
                FileInputStream fis = new FileInputStream(wordlistFile);
                wordlistConfig = yaml.load(fis);
                fis.close();
            }

            // Load main config
            File mainConfigFile = new File(dataDirectory.toFile(), "chatfilter-config.yml");
            if (mainConfigFile.exists()) {
                FileInputStream fis = new FileInputStream(mainConfigFile);
                mainConfig = yaml.load(fis);
                fis.close();
            }

        } catch (Exception e) {
            logger.error("Failed to load chat filter configs: " + e.getMessage());
            return;
        }

        if (mainConfig == null) mainConfig = new HashMap<>();
        if (regexConfig == null) regexConfig = new HashMap<>();
        if (wordlistConfig == null) wordlistConfig = new HashMap<>();

        // Load settings from main config
        enabled = getBoolean(mainConfig, "enabled", true);
        prefix = getString(mainConfig, "prefix", "<dark_red>[ChatFilter]</dark_red> ");

        // Add global blocked words
        List<String> globalBlockedWords = getStringList(mainConfig, "blocked-words");
        if (globalBlockedWords != null && !globalBlockedWords.isEmpty()) {
            blockedWords.addAll(globalBlockedWords);
            logger.info("Added " + globalBlockedWords.size() + " global blocked words from main config");
        }

        // Load regex patterns if enabled
        if (getBoolean(regexConfig, "Enable-regex", true)) {
            loadPatterns();
        }

        // Load wordlist if enabled
        if (getBoolean(wordlistConfig, "enabled", true)) {
            List<String> wordList = getStringList(wordlistConfig, "list");
            if (wordList != null && !wordList.isEmpty()) {
                blockedWords.addAll(wordList);
                logger.info("Added " + wordList.size() + " blocked words from wordlist.yml");
            }
        }

        // Log the loaded configuration
        logger.info("ChatFilter loaded " + blockedWords.size() + " total blocked words");
        logger.info("ChatFilter prefix: " + prefix);
        
        // Debug log all words if debug mode is enabled
        if (getBoolean(mainConfig, "debug", false)) {
            logger.info("DEBUG: All blocked words:");
            for (String word : blockedWords) {
                logger.info(" - \"" + word + "\"");
            }
        }
    }

    private void loadPatterns() {
        // Load IP blocking patterns
        if (getNestedBoolean(regexConfig, "block-ips.enabled", true)) {
            try {
                String ipRegex = getNestedString(regexConfig, "block-ips.regex", "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
                
                logger.info("Compiling IP regex: " + ipRegex);
                try {
                    Pattern ipPattern = Pattern.compile(ipRegex, Pattern.CASE_INSENSITIVE);
                    ipPatterns.add(ipPattern);
                    logger.info("Successfully compiled IP regex pattern");
                } catch (Exception e) {
                    logger.warn("Failed to compile IP pattern: " + e.getMessage());
                }
                
                String advancedRegex = getNestedString(regexConfig, "block-ips.advanced-regex", null);
                if (advancedRegex != null && !advancedRegex.isEmpty()) {
                    logger.info("Compiling advanced IP regex: " + advancedRegex);
                    try {
                        Pattern advancedPattern = Pattern.compile(advancedRegex, Pattern.CASE_INSENSITIVE);
                        ipPatterns.add(advancedPattern);
                        logger.info("Successfully compiled advanced IP regex pattern");
                    } catch (Exception e) {
                        logger.warn("Failed to compile advanced IP pattern: " + e.getMessage());
                    }
                }
                
                if (ipPatterns.isEmpty()) {
                    // Add a simple fallback pattern to catch IPs like 127.0.0.1
                    ipPatterns.add(Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", Pattern.CASE_INSENSITIVE));
                    logger.info("Added fallback IP pattern");
                }
                
                logger.info("Loaded " + ipPatterns.size() + " IP blocking patterns");
            } catch (Exception e) {
                logger.warn("Error in IP blocking pattern section: " + e.getMessage());
            }
        }

        // Load spam character patterns
        if (getNestedBoolean(regexConfig, "block-spam-chars.enabled", true)) {
            try {
                int threshold = getNestedInt(regexConfig, "block-spam-chars.threshold", 4);
                String spamRegex = getNestedString(regexConfig, "block-spam-chars.regex", "(.)\\1{" + (threshold - 1) + ",}");
                
                logger.info("Compiling spam regex: " + spamRegex);
                try {
                    Pattern spamPattern = Pattern.compile(spamRegex, Pattern.CASE_INSENSITIVE);
                    spamPatterns.add(spamPattern);
                    logger.info("Successfully compiled spam regex pattern");
                } catch (Exception e) {
                    logger.warn("Failed to compile spam pattern: " + e.getMessage());
                }
                
                logger.info("Loaded spam prevention patterns");
            } catch (Exception e) {
                logger.warn("Error in spam character pattern section: " + e.getMessage());
            }
        }

        // Load caps patterns
        if (getNestedBoolean(regexConfig, "block-caps.enabled", true)) {
            try {
                int threshold = getNestedInt(regexConfig, "block-caps.threshold", 60);
                String capsRegex = "(?=.{8,})(?=(?:.*[A-Z]){" + threshold + ",}).*";
                
                logger.info("Compiling caps regex: " + capsRegex);
                try {
                    Pattern capsPattern = Pattern.compile(capsRegex);
                    capsPatterns.add(capsPattern);
                    logger.info("Successfully compiled caps regex pattern");
                } catch (Exception e) {
                    logger.warn("Failed to compile caps pattern: " + e.getMessage());
                }
            } catch (Exception e) {
                logger.warn("Error in caps pattern section: " + e.getMessage());
            }
        }

        // Load custom patterns
        if (getNestedBoolean(regexConfig, "custom-patterns.enabled", false)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> patternsSection = (Map<String, Object>) getNestedValue(regexConfig, "custom-patterns.patterns");
            if (patternsSection != null) {
                for (Map.Entry<String, Object> entry : patternsSection.entrySet()) {
                    String key = entry.getKey();
                    String pattern = entry.getValue().toString();
                    try {
                        if (pattern != null && !pattern.isEmpty()) {
                            logger.info("Compiling custom regex: " + key + " = " + pattern);
                            try {
                                Pattern customPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                                customPatterns.add(customPattern);
                                logger.info("Successfully loaded custom pattern: " + key);
                            } catch (Exception e) {
                                logger.warn("Failed to compile custom pattern '" + key + "': " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Error with custom regex pattern '" + key + "': " + e.getMessage());
                    }
                }
            } else {
                logger.info("No custom patterns section found in config");
            }
        } else {
            logger.info("Custom patterns are disabled in config");
        }
        
        logger.info("Loaded " + ipPatterns.size() + " IP patterns, " + 
                              spamPatterns.size() + " spam patterns, " +
                              capsPatterns.size() + " caps patterns, " +
                              customPatterns.size() + " custom patterns");
    }

    @Subscribe
    public EventTask onChat(PlayerChatEvent event) {
        return EventTask.async(() -> {
            // Debug log the initial state
            logger.info("ChatFilter processing message from " + event.getPlayer().getUsername());

            // Check if module is enabled
            if (!enabled) {
                logger.info("ChatFilter module is disabled in config");
                return;
            }

            String wordlistBypassPermission = getString(wordlistConfig, "bypass-permission", "skyenetv.wordlist.bypass");
            String regexBypassPermission = getString(regexConfig, "bypass-permission", "skyenetv.regex.bypass");

            // Check wordlist filtering
            if (!event.getPlayer().hasPermission(wordlistBypassPermission) && 
                getNestedBoolean(mainConfig, "modules.ChatFilter.wordlist.enabled", true)) {
                
                logger.info("Checking message against wordlist. Words loaded: " + blockedWords.size());
                String wordlistMsg = getString(wordlistConfig, "blocked-message", "<prefix>Your message was filtered for containing a blocked word: <word>");

                String currentMessage = event.getMessage();
                for (String word : blockedWords) {
                    if (word == null || word.isEmpty()) {
                        continue;
                    }

                    String lowercaseWord = word.toLowerCase();
                    String lowercaseMessage = currentMessage.toLowerCase();

                    logger.info("Checking for word: \"" + word + "\"");

                    if (lowercaseMessage.contains(lowercaseWord)) {
                        logger.info("Found blocked word: \"" + word + "\"");

                        // Replace the entire message
                        event.setResult(PlayerChatEvent.ChatResult.denied());
                        
                        // Send notification to player
                        Component message = miniMessage.deserialize(
                            wordlistMsg.replace("<prefix>", prefix).replace("<word>", word)
                        );
                        event.getPlayer().sendMessage(message);
                        return;
                    }
                }
            } else if (event.getPlayer().hasPermission(wordlistBypassPermission)) {
                logger.info("Wordlist check skipped - player has bypass permission: " + wordlistBypassPermission);
            }

            // Check regex patterns
            if (!event.getPlayer().hasPermission(regexBypassPermission) && 
                getNestedBoolean(mainConfig, "modules.ChatFilter.regex.enabled", true)) {
                
                logger.info("Checking regex patterns");
                String regexMsg = getString(regexConfig, "blocked-message", "<prefix>Your message was filtered by pattern: <pattern>");
                String originalMessage = event.getMessage();

                // Check IP patterns
                if (getNestedBoolean(regexConfig, "block-ips.enabled", true)) {
                    logger.info("Checking IP patterns (" + ipPatterns.size() + " patterns)");
                    
                    // Direct IP pattern matching
                    if (originalMessage.matches(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*")) {
                        logger.info("Direct IP pattern match found!");
                        
                        event.setResult(PlayerChatEvent.ChatResult.denied());
                        
                        Component message = miniMessage.deserialize(
                            regexMsg.replace("<prefix>", prefix)
                                   .replace("<pattern>", "IP Address")
                        );
                        event.getPlayer().sendMessage(message);
                        return;
                    }
                    
                    // Try loaded patterns
                    for (Pattern pattern : ipPatterns) {
                        if (checkPattern(originalMessage, pattern, "IP Address")) {
                            event.setResult(PlayerChatEvent.ChatResult.denied());
                            Component message = miniMessage.deserialize(
                                regexMsg.replace("<prefix>", prefix)
                                       .replace("<pattern>", "IP Address")
                            );
                            event.getPlayer().sendMessage(message);
                            logger.info("IP pattern matched and filtered");
                            return;
                        }
                    }
                }

                // Check spam patterns
                if (getNestedBoolean(regexConfig, "block-spam-chars.enabled", true)) {
                    logger.info("Checking spam patterns (" + spamPatterns.size() + " patterns)");
                    
                    // Direct spam check for repeated characters
                    char lastChar = '\0';
                    int count = 1;
                    int threshold = getNestedInt(regexConfig, "block-spam-chars.threshold", 4);
                    
                    for (int i = 0; i < originalMessage.length(); i++) {
                        char c = originalMessage.charAt(i);
                        if (c == lastChar) {
                            count++;
                            if (count >= threshold) {
                                logger.info("Direct spam pattern found!");
                                
                                event.setResult(PlayerChatEvent.ChatResult.denied());
                                
                                Component message = miniMessage.deserialize(
                                    regexMsg.replace("<prefix>", prefix)
                                           .replace("<pattern>", "Character Spam")
                                );
                                event.getPlayer().sendMessage(message);
                                logger.info("Applied direct spam replacement");
                                return;
                            }
                        } else {
                            lastChar = c;
                            count = 1;
                        }
                    }
                    
                    // Try loaded patterns
                    for (Pattern pattern : spamPatterns) {
                        if (checkPattern(originalMessage, pattern, "Character Spam")) {
                            event.setResult(PlayerChatEvent.ChatResult.denied());
                            Component message = miniMessage.deserialize(
                                regexMsg.replace("<prefix>", prefix)
                                       .replace("<pattern>", "Character Spam")
                            );
                            event.getPlayer().sendMessage(message);
                            logger.info("Spam pattern matched and filtered");
                            return;
                        }
                    }
                }

                // Check caps patterns
                if (getNestedBoolean(regexConfig, "block-caps.enabled", true)) {
                    logger.info("Checking caps patterns");
                    int minLength = getNestedInt(regexConfig, "block-caps.min-length", 6);
                    int threshold = getNestedInt(regexConfig, "block-caps.threshold", 60);
                    
                    if (originalMessage.length() >= minLength) {
                        int capsCount = 0;
                        int letterCount = 0;
                        
                        for (char c : originalMessage.toCharArray()) {
                            if (Character.isLetter(c)) {
                                letterCount++;
                                if (Character.isUpperCase(c)) {
                                    capsCount++;
                                }
                            }
                        }
                        
                        if (letterCount > 0) {
                            double capsPercentage = (capsCount * 100.0) / letterCount;
                            logger.info("Caps percentage: " + capsPercentage + "% (threshold: " + threshold + "%)");
                            
                            if (capsPercentage > threshold) {
                                logger.info("Caps threshold exceeded!");
                                
                                event.setResult(PlayerChatEvent.ChatResult.denied());
                                
                                Component message = miniMessage.deserialize(
                                    regexMsg.replace("<prefix>", prefix)
                                           .replace("<pattern>", "Excessive Caps")
                                );
                                event.getPlayer().sendMessage(message);
                                return;
                            }
                        }
                    }
                }

                // Check custom patterns
                if (getNestedBoolean(regexConfig, "custom-patterns.enabled", false)) {
                    logger.info("Checking custom patterns (" + customPatterns.size() + " patterns)");
                    
                    if (customPatterns.isEmpty()) {
                        logger.info("No custom patterns loaded");
                    } else {
                        // Try direct match first with a test custom regex for badwords
                        if (originalMessage.toLowerCase().matches(".*\\b(badword\\d+)\\b.*")) {
                            logger.info("Direct custom pattern found!");
                            
                            event.setResult(PlayerChatEvent.ChatResult.denied());
                            
                            Component message = miniMessage.deserialize(
                                regexMsg.replace("<prefix>", prefix)
                                       .replace("<pattern>", "Custom Pattern")
                            );
                            event.getPlayer().sendMessage(message);
                            return;
                        } else {
                            // Try loaded patterns
                            for (Pattern pattern : customPatterns) {
                                if (checkPattern(originalMessage, pattern, "Custom Pattern")) {
                                    event.setResult(PlayerChatEvent.ChatResult.denied());
                                    Component message = miniMessage.deserialize(
                                        regexMsg.replace("<prefix>", prefix)
                                               .replace("<pattern>", "Custom Pattern")
                                    );
                                    event.getPlayer().sendMessage(message);
                                    logger.info("Custom pattern matched and filtered");
                                    return;
                                }
                            }
                        }
                    }
                }
            } else if (event.getPlayer().hasPermission(regexBypassPermission)) {
                logger.info("Regex check skipped - player has bypass permission: " + regexBypassPermission);
            }
        });
    }

    private boolean checkPattern(String text, Pattern pattern, String type) {
        logger.info("Checking pattern: " + pattern.pattern());
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            logger.info("Pattern matched: " + matcher.group());
            return true;
        }
        return false;
    }

    // Helper methods for safe config access
    private String getString(Map<String, Object> config, String key, String defaultValue) {
        Object value = config.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private boolean getBoolean(Map<String, Object> config, String key, boolean defaultValue) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringList(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }

    private Object getNestedValue(Map<String, Object> config, String path) {
        String[] keys = path.split("\\.");
        Object current = config;
        
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
            } else {
                return null;
            }
        }
        
        return current;
    }

    private String getNestedString(Map<String, Object> config, String path, String defaultValue) {
        Object value = getNestedValue(config, path);
        return value != null ? value.toString() : defaultValue;
    }

    private boolean getNestedBoolean(Map<String, Object> config, String path, boolean defaultValue) {
        Object value = getNestedValue(config, path);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    private int getNestedInt(Map<String, Object> config, String path, int defaultValue) {
        Object value = getNestedValue(config, path);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
}
