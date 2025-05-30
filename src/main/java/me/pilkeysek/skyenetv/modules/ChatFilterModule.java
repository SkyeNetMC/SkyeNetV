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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ChatFilterModule {
    private final Logger logger;
    private final Path dataDirectory;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private Map<String, Object> regexConfig;
    private Map<String, Object> wordlistConfig;
    private final List<Pattern> spamPatterns = new ArrayList<>();
    private final List<Pattern> ipPatterns = new ArrayList<>(); 
    private final List<Pattern> capsPatterns = new ArrayList<>();
    private final List<Pattern> customPatterns = new ArrayList<>();
    private final List<Pattern> asciiPatterns = new ArrayList<>();
    private final List<Pattern> urlPatterns = new ArrayList<>();
    private final List<String> blockedWords = new ArrayList<>();
    private final String configFolder = "filters";
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
        // Create filters folder in plugin data directory
        File folder = new File(dataDirectory.toFile(), configFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Copy regex.yml from resources if it doesn't exist
        File regexFile = new File(folder, "regex.yml");
        if (!regexFile.exists()) {
            try {
                copyResourceToFile("filters/regex.yml", regexFile);
                logger.info("Created default regex configuration from template.");
            } catch (Exception e) {
                logger.warn("Failed to create regex.yml: " + e.getMessage());
            }
        }

        // Copy wordlist.yml from resources if it doesn't exist
        File wordlistFile = new File(folder, "wordlist.yml");
        if (!wordlistFile.exists()) {
            try {
                copyResourceToFile("filters/wordlist.yml", wordlistFile);
                logger.info("Created default wordlist configuration from template.");
            } catch (Exception e) {
                logger.warn("Failed to create wordlist.yml: " + e.getMessage());
            }
        }
    }

    private void copyResourceToFile(String resourcePath, File targetFile) throws IOException {
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (resourceStream != null) {
            Files.copy(resourceStream, targetFile.toPath());
            resourceStream.close();
        } else {
            logger.warn("Resource " + resourcePath + " not found in plugin JAR");
            createFallbackConfig(targetFile);
        }
    }

    private void createFallbackConfig(File file) throws IOException {
        if (file.getName().equals("regex.yml")) {
            createFallbackRegexConfig(file);
        } else if (file.getName().equals("wordlist.yml")) {
            createFallbackWordlistConfig(file);
        }
    }

    private void createFallbackRegexConfig(File file) throws IOException {
        String content = """
            # SkyeNetV Chat Filter Configuration
            Enable-regex: true
            prefix: "<dark_red>[ChatFilter]</dark_red> "
            blocked-message: "<prefix>Your message was filtered by pattern: <pattern>"
            replacement-text: "<red>[Filtered]</red>"
            
            block-ips:
              enabled: true
              regex: "\\\\b(?:\\\\d{1,3}\\\\.){3}\\\\d{1,3}\\\\b"
              
            block-spam-chars:
              enabled: true
              regex: "(.)\\\\1{4,}"
              threshold: 4
              
            block-caps:
              enabled: true
              min-length: 6
              threshold: 60
              
            custom-patterns:
              enabled: true
              patterns:
                badword-pattern: "badword"
            """;
        
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    private void createFallbackWordlistConfig(File file) throws IOException {
        String content = """
            # SkyeNetV Chat Filter - Wordlist Configuration
            enabled: true
            blocked-message: "<prefix>Your message was filtered for containing a blocked word: <word>"
            list:
              - badword
              - spam
              - test
            """;
        
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }

    private void loadConfigs() {
        // Clear all existing data
        spamPatterns.clear();
        ipPatterns.clear();
        capsPatterns.clear();
        customPatterns.clear();
        asciiPatterns.clear();
        urlPatterns.clear();
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

        } catch (Exception e) {
            logger.error("Failed to load chat filter configs: " + e.getMessage());
            return;
        }

        if (regexConfig == null) regexConfig = new HashMap<>();
        if (wordlistConfig == null) wordlistConfig = new HashMap<>();

        // Load settings
        enabled = getBoolean(regexConfig, "Enable-regex", true);
        prefix = getString(regexConfig, "prefix", "<dark_red>[ChatFilter]</dark_red> ");

        // Load regex patterns if enabled
        if (enabled) {
            loadPatterns();
        }

        // Load wordlist if enabled
        if (getBoolean(wordlistConfig, "enabled", true)) {
            List<String> wordList = getStringList(wordlistConfig, "list");
            if (wordList != null && !wordList.isEmpty()) {
                blockedWords.addAll(wordList);
                logger.info("Loaded " + wordList.size() + " blocked words");
            }
        }

        logger.info("ChatFilter loaded with " + blockedWords.size() + " blocked words and " +
                   (ipPatterns.size() + spamPatterns.size() + capsPatterns.size() + 
                    customPatterns.size() + asciiPatterns.size() + urlPatterns.size()) + " patterns");
    }

    private void loadPatterns() {
        // Load IP patterns
        if (getNestedBoolean(regexConfig, "block-ips.enabled", true)) {
            String ipRegex = getNestedString(regexConfig, "block-ips.regex", "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
            compileAndAddPattern(ipRegex, ipPatterns, "IP");
            
            String advancedIpRegex = getNestedString(regexConfig, "block-ips.advanced-regex", null);
            if (advancedIpRegex != null && !advancedIpRegex.isEmpty()) {
                compileAndAddPattern(advancedIpRegex, ipPatterns, "Advanced IP");
            }
        }

        // Load spam patterns
        if (getNestedBoolean(regexConfig, "block-spam-chars.enabled", true)) {
            String spamRegex = getNestedString(regexConfig, "block-spam-chars.regex", "(.)\\1{4,}");
            compileAndAddPattern(spamRegex, spamPatterns, "Spam");
        }

        // Load ASCII art patterns
        if (getNestedBoolean(regexConfig, "block-ascii-art.enabled", true)) {
            String asciiRegex = getNestedString(regexConfig, "block-ascii-art.regex", 
                "(?:(?:\\s*[^\\w\\s]{2,}\\s*){3,})|(?:[^\\w\\s]{10,})");
            compileAndAddPattern(asciiRegex, asciiPatterns, "ASCII Art");
        }

        // Load URL patterns
        if (getNestedBoolean(regexConfig, "block-urls.enabled", true)) {
            String urlRegex = getNestedString(regexConfig, "block-urls.regex", 
                "https?://[\\w\\.-]+\\.[a-zA-Z]{2,}");
            compileAndAddPattern(urlRegex, urlPatterns, "URL");
        }

        // Load custom patterns
        if (getNestedBoolean(regexConfig, "custom-patterns.enabled", true)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> patternsSection = (Map<String, Object>) getNestedValue(regexConfig, "custom-patterns.patterns");
            if (patternsSection != null) {
                for (Map.Entry<String, Object> entry : patternsSection.entrySet()) {
                    String pattern = entry.getValue().toString();
                    compileAndAddPattern(pattern, customPatterns, "Custom: " + entry.getKey());
                }
            }
        }
    }

    private void compileAndAddPattern(String regex, List<Pattern> patternList, String type) {
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            patternList.add(pattern);
            logger.info("Loaded " + type + " pattern: " + regex);
        } catch (Exception e) {
            logger.warn("Failed to compile " + type + " pattern '" + regex + "': " + e.getMessage());
        }
    }

    @Subscribe
    public EventTask onChat(PlayerChatEvent event) {
        return EventTask.async(() -> {
            if (!enabled) {
                return;
            }

            String message = event.getMessage();
            String playerName = event.getPlayer().getUsername();
            
            logger.info("Processing message from " + playerName + ": " + message);

            // Check wordlist
            if (getBoolean(wordlistConfig, "enabled", true) && 
                !event.getPlayer().hasPermission("skyenetv.wordlist.bypass")) {
                
                for (String word : blockedWords) {
                    if (word != null && !word.isEmpty() && 
                        message.toLowerCase().contains(word.toLowerCase())) {
                        
                        logger.info("Blocked message from " + playerName + " for word: " + word);
                        event.setResult(PlayerChatEvent.ChatResult.denied());
                        
                        String blockedMsg = getString(wordlistConfig, "blocked-message", 
                            "<prefix>Your message was filtered for containing a blocked word: <word>");
                        Component notification = miniMessage.deserialize(
                            blockedMsg.replace("<prefix>", prefix).replace("<word>", word));
                        event.getPlayer().sendMessage(notification);
                        return;
                    }
                }
            }

            // Check regex patterns
            if (!event.getPlayer().hasPermission("skyenetv.regex.bypass")) {
                String blockedMsg = getString(regexConfig, "blocked-message", 
                    "<prefix>Your message was filtered by pattern: <pattern>");

                if (checkPatterns(message, ipPatterns, "IP Address") ||
                    checkPatterns(message, spamPatterns, "Spam Characters") ||
                    checkPatterns(message, asciiPatterns, "ASCII Art") ||
                    checkPatterns(message, urlPatterns, "URL") ||
                    checkPatterns(message, customPatterns, "Custom Pattern")) {
                    
                    logger.info("Blocked message from " + playerName + " for regex pattern");
                    event.setResult(PlayerChatEvent.ChatResult.denied());
                    
                    Component notification = miniMessage.deserialize(
                        blockedMsg.replace("<prefix>", prefix).replace("<pattern>", "Pattern"));
                    event.getPlayer().sendMessage(notification);
                    return;
                }

                // Check caps
                if (getNestedBoolean(regexConfig, "block-caps.enabled", true)) {
                    int minLength = getNestedInt(regexConfig, "block-caps.min-length", 6);
                    int threshold = getNestedInt(regexConfig, "block-caps.threshold", 60);
                    
                    if (message.length() >= minLength && isExcessiveCaps(message, threshold)) {
                        logger.info("Blocked message from " + playerName + " for excessive caps");
                        event.setResult(PlayerChatEvent.ChatResult.denied());
                        
                        Component notification = miniMessage.deserialize(
                            blockedMsg.replace("<prefix>", prefix).replace("<pattern>", "Excessive Caps"));
                        event.getPlayer().sendMessage(notification);
                        return;
                    }
                }
            }
        });
    }

    private boolean checkPatterns(String message, List<Pattern> patterns, String type) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(message).find()) {
                logger.info("Pattern matched: " + type);
                return true;
            }
        }
        return false;
    }

    private boolean isExcessiveCaps(String message, int threshold) {
        int capsCount = 0;
        int letterCount = 0;
        
        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
                if (Character.isUpperCase(c)) {
                    capsCount++;
                }
            }
        }
        
        if (letterCount == 0) return false;
        
        double capsPercentage = (capsCount * 100.0) / letterCount;
        return capsPercentage > threshold;
    }

    /**
     * Filter a message and return the result with hover information
     * @param message The message to filter
     * @param playerName The name of the player (for logging)
     * @return FilterResult containing filtered message and metadata
     */
    public FilterResult filterMessage(String message, String playerName) {
        if (!enabled) {
            return new FilterResult(message, false, null, null);
        }
        
        logger.info("Filtering message from " + playerName + ": " + message);
        
        // Check wordlist
        if (getBoolean(wordlistConfig, "enabled", true)) {
            for (String word : blockedWords) {
                if (word != null && !word.isEmpty() && 
                    message.toLowerCase().contains(word.toLowerCase())) {
                    
                    logger.info("Blocked message from " + playerName + " for word: " + word);
                    String replacement = getString(regexConfig, "replacement-text", "<red>[Filtered]</red>");
                    return new FilterResult(replacement, true, "Blocked Word: " + word, message);
                }
            }
        }
        
        // Check regex patterns
        if (checkPatterns(message, ipPatterns, "IP Address")) {
            String replacement = getString(regexConfig, "replacement-text", "<red>[Filtered]</red>");
            return new FilterResult(replacement, true, "IP Address detected", message);
        }
        
        if (checkPatterns(message, spamPatterns, "Spam Characters")) {
            String replacement = getString(regexConfig, "replacement-text", "<red>[Filtered]</red>");
            return new FilterResult(replacement, true, "Spam characters detected", message);
        }
        
        if (checkPatterns(message, asciiPatterns, "ASCII Art")) {
            String replacement = getString(regexConfig, "replacement-text", "<red>[Filtered]</red>");
            return new FilterResult(replacement, true, "ASCII art detected", message);
        }
        
        if (checkPatterns(message, urlPatterns, "URL")) {
            String replacement = getString(regexConfig, "replacement-text", "<red>[Filtered]</red>");
            return new FilterResult(replacement, true, "URL detected", message);
        }
        
        if (checkPatterns(message, customPatterns, "Custom Pattern")) {
            String replacement = getString(regexConfig, "replacement-text", "<red>[Filtered]</red>");
            return new FilterResult(replacement, true, "Custom pattern detected", message);
        }
        
        // Check caps
        if (getNestedBoolean(regexConfig, "block-caps.enabled", true)) {
            int minLength = getNestedInt(regexConfig, "block-caps.min-length", 6);
            int threshold = getNestedInt(regexConfig, "block-caps.threshold", 60);
            
            if (message.length() >= minLength && isExcessiveCaps(message, threshold)) {
                String replacement = getString(regexConfig, "replacement-text", "<red>[Filtered]</red>");
                return new FilterResult(replacement, true, "Excessive caps detected", message);
            }
        }
        
        return new FilterResult(message, false, null, null);
    }
    
    /**
     * Result of message filtering
     */
    public static class FilterResult {
        private final String filteredMessage;
        private final boolean wasFiltered;
        private final String filterReason;
        private final String originalMessage;
        
        public FilterResult(String filteredMessage, boolean wasFiltered, String filterReason, String originalMessage) {
            this.filteredMessage = filteredMessage;
            this.wasFiltered = wasFiltered;
            this.filterReason = filterReason;
            this.originalMessage = originalMessage;
        }
        
        public String getFilteredMessage() { return filteredMessage; }
        public boolean wasFiltered() { return wasFiltered; }
        public String getFilterReason() { return filterReason; }
        public String getOriginalMessage() { return originalMessage; }
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
