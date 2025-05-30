package me.pilkeysek.skyenetv.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class RulesConfig {
    private String header;
    private String footer;
    private List<Rule> rules;
    private final Path configPath;
    private final Logger logger;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public RulesConfig(Path dataDirectory, Logger logger) {
        this.configPath = dataDirectory.resolve("rules.json");
        this.logger = logger;
        this.rules = new ArrayList<>();
        loadConfig();
    }

    public void loadConfig() {
        // Check if the config file exists
        File configFile = configPath.toFile();
        if (!configFile.exists()) {
            // Copy default config from resources
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("rules.json")) {
                if (is != null) {
                    Files.createDirectories(configPath.getParent());
                    Files.copy(is, configPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    logger.error("Could not find default rules.json in resources");
                    createDefaultConfig();
                }
            } catch (IOException e) {
                logger.error("Failed to copy default rules config", e);
                createDefaultConfig();
            }
        }

        // Load the config
        try (Reader reader = Files.newBufferedReader(configPath)) {
            RulesConfig loadedConfig = GSON.fromJson(reader, RulesConfig.class);
            this.header = loadedConfig.getHeader();
            this.footer = loadedConfig.getFooter();
            this.rules = loadedConfig.getRules();
        } catch (Exception e) {
            logger.error("Failed to load rules config", e);
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        // Create a default config if loading fails
        this.header = "<gradient:gold:yellow>SkyeNet Rules</gradient>";
        this.footer = "<gray>Thank you for following our rules!</gray>";
        this.rules = new ArrayList<>();
        
        this.rules.add(new Rule(1, "<red>No Hacking</red>", 
                "<gray>Using any modified clients, cheats or unfair advantages is strictly prohibited.</gray>"));
        this.rules.add(new Rule(2, "<red>Respect All Players</red>", 
                "<gray>Be respectful to everyone. Harassment, discrimination, and hate speech will not be tolerated.</gray>"));
        
        saveConfig();
    }

    public void saveConfig() {
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(this, writer);
            logger.info("Rules configuration saved successfully");
        } catch (IOException e) {
            logger.error("Failed to save rules config", e);
            throw new RuntimeException("Failed to save rules configuration", e);
        }
    }

    // Getters and Setters
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
        saveConfig();
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
        saveConfig();
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
        saveConfig();
    }

    public Rule getRule(int id) {
        for (Rule rule : rules) {
            if (rule.getId() == id) {
                return rule;
            }
        }
        return null;
    }

    public void addRule(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Rule cannot be null");
        }
        if (rule.getTitle() == null || rule.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Rule title cannot be null or empty");
        }
        if (rule.getDescription() == null || rule.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Rule description cannot be null or empty");
        }
        
        // Find the maximum ID
        int maxId = 0;
        for (Rule r : rules) {
            if (r.getId() > maxId) {
                maxId = r.getId();
            }
        }
        // Set new ID if not already set
        if (rule.getId() <= 0) {
            rule.setId(maxId + 1);
        }
        
        rules.add(rule);
        logger.info("Adding new rule with ID {} and title: {}", rule.getId(), rule.getTitle());
        saveConfig();
    }

    public boolean removeRule(int id) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).getId() == id) {
                rules.remove(i);
                saveConfig();
                return true;
            }
        }
        return false;
    }

    public boolean updateRule(int id, String title, String description) {
        for (Rule rule : rules) {
            if (rule.getId() == id) {
                rule.setTitle(title);
                rule.setDescription(description);
                saveConfig();
                return true;
            }
        }
        return false;
    }

    // Rule class
    public static class Rule {
        private int id;
        private String title;
        private String description;

        public Rule() {
        }

        public Rule(int id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
