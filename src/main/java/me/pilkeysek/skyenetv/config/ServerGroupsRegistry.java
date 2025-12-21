package me.pilkeysek.skyenetv.config;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Loads and exposes per-server/group delivery rules from `server_groups.yml`.
 *
 * This implementation provides a simple per-sender rule set where each sender
 * server id may define a rule with `enabled`, `type` (whitelist|blacklist) and
 * `list` of server names. The {@link #shouldDeliver} method applies those rules.
 */
public class ServerGroupsRegistry {

    private final Path dataDirectory;
    private final Logger logger;
    private final File groupsFile;
    private final Map<String, ServerGroup> rules = new HashMap<>();
    // map serverName (lowercase) -> list of group ids (lowercase)
    private Map<String, List<String>> serverToGroups = new HashMap<>();

    
    public List<String> getGroupsForServer(String server) {
        if (server == null) return new ArrayList<>();
        return serverToGroups.getOrDefault(server.toLowerCase(Locale.ROOT), new ArrayList<>());
    }
    public boolean areInSameGroup(String s1, String s2) {
        if (s1 == null || s2 == null) return false;
        List<String> g1 = getGroupsForServer(s1);
        List<String> g2 = getGroupsForServer(s2);
        for (String g : g1) {
            if (g2.contains(g)) return true;
        }
        return false;
    }

    public java.util.Optional<String> getJoinMessageForServer(String server) {
        for (String gid : getGroupsForServer(server)) {
            ServerGroup sg = rules.get(gid.toLowerCase(Locale.ROOT));
            if (sg != null && sg.getJoinMessage() != null && !sg.getJoinMessage().isEmpty()) {
                return java.util.Optional.of(sg.getJoinMessage());
            }
        }
        return java.util.Optional.empty();
    }

    public java.util.Optional<String> getLeaveMessageForServer(String server) {
        for (String gid : getGroupsForServer(server)) {
            ServerGroup sg = rules.get(gid.toLowerCase(Locale.ROOT));
            if (sg != null && sg.getLeaveMessage() != null && !sg.getLeaveMessage().isEmpty()) {
                return java.util.Optional.of(sg.getLeaveMessage());
            }
        }
        return java.util.Optional.empty();
    }

    /**
     * Whether messages originating on the given server should be forwarded to Discord.
     * If the server is in any group and any of those groups has discord=true, returns true.
     * If the server is in groups but none have discord=true returns false.
     * If the server is in no groups, returns true (backwards-compatible default).
     */
    public boolean shouldSendServerToDiscord(String serverId) {
        List<String> groups = getGroupsForServer(serverId);
        if (groups.isEmpty()) return true; // default allow
        for (String gid : groups) {
            ServerGroup sg = rules.get(gid.toLowerCase(Locale.ROOT));
            if (sg != null && sg.isEnabled() && sg.isDiscord()) return true;
        }
        return false;
    }

    public boolean shouldHandleEvent(String senderServer, MessageType type) {
        if (senderServer == null) return true;
        List<String> groups = getGroupsForServer(senderServer);
        if (groups.isEmpty()) return true; // fallback to existing behavior
        for (String gid : groups) {
            ServerGroup sg = rules.get(gid.toLowerCase(Locale.ROOT));
            if (sg == null) continue;
            String events = sg.getEvents();
            if (events != null && "custom".equalsIgnoreCase(events)) return true;
            // if join/leave messages exist, treat as custom
            if (type == MessageType.JOIN && sg.getJoinMessage() != null && !sg.getJoinMessage().isEmpty()) return true;
            if (type == MessageType.LEAVE && sg.getLeaveMessage() != null && !sg.getLeaveMessage().isEmpty()) return true;
        }
        return false;
    }

    public ServerGroupsRegistry(Path dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.groupsFile = new File(dataDirectory.toFile(), "server_groups.yml");
        load();
    }

    private void saveDefault() {
        try {
            if (!groupsFile.getParentFile().exists()) {
                groupsFile.getParentFile().mkdirs();
            }

            InputStream defaultStream = getClass().getResourceAsStream("/server_groups.yml");
            if (defaultStream != null) {
                Files.copy(defaultStream, groupsFile.toPath());
                defaultStream.close();
                logger.info("Created default server_groups.yml from resources");
                return;
            }

            // Fallback minimal default
            Map<String, Object> defaultMap = new HashMap<>();
            Map<String, Object> perServer = new HashMap<>();
            Map<String, Object> defaultRule = new HashMap<>();
            defaultRule.put("enabled", true);
            defaultRule.put("type", "whitelist");
            defaultRule.put("list", new ArrayList<>());
            perServer.put("default", defaultRule);
            defaultMap.put("per_server", perServer);

            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(groupsFile);
            yaml.dump(defaultMap, writer);
            writer.close();
            logger.info("Created fallback server_groups.yml at: {}", groupsFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to create default server_groups.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void load() {
        try {
            if (!groupsFile.exists()) {
                saveDefault();
            }

            Yaml yaml = new Yaml();
            FileInputStream fis = new FileInputStream(groupsFile);
            Object loaded = yaml.load(fis);
            fis.close();

            // Reset
            rules.clear();
            Map<String, List<String>> serverToGroups = new HashMap<>();

            if (!(loaded instanceof Map)) {
                logger.warn("server_groups.yml did not contain a mapping; skipping");
                return;
            }

            Map<String, Object> top = (Map<String, Object>) loaded;

            // Legacy `per_server` handling (keep backward compatibility)
            Object perServerObj = top.get("per_server");
            if (perServerObj instanceof Map) {
                Map<String, Object> perServer = (Map<String, Object>) perServerObj;
                for (Map.Entry<String, Object> e : perServer.entrySet()) {
                    String key = e.getKey();
                    Object v = e.getValue();
                    if (!(v instanceof Map)) continue;
                    Map<String, Object> map = (Map<String, Object>) v;
                    ServerGroup sg = new ServerGroup();
                    sg.setId(key);
                    Object enabled = map.get("enabled");
                    if (enabled instanceof Boolean) sg.setEnabled((Boolean) enabled);
                    Object type = map.get("type");
                    if (type != null) sg.setType(type.toString());
                    Object list = map.get("list");
                    if (list instanceof List) {
                        List<String> servers = new ArrayList<>();
                        for (Object o : (List<Object>) list) {
                            if (o != null) servers.add(o.toString());
                        }
                        sg.setList(servers);
                        // map servers to this legacy key
                        for (String s : servers) {
                            serverToGroups.computeIfAbsent(s.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(key.toLowerCase(Locale.ROOT));
                        }
                    }
                    rules.put(key.toLowerCase(Locale.ROOT), sg);
                }
            }

            // Group-based parsing: any top-level key that's not `per_server` is a group id
            for (Map.Entry<String, Object> entry : top.entrySet()) {
                String groupId = entry.getKey();
                if ("per_server".equalsIgnoreCase(groupId)) continue;
                Object v = entry.getValue();
                if (!(v instanceof Map)) continue;
                Map<String, Object> map = (Map<String, Object>) v;

                ServerGroup sg = new ServerGroup();
                sg.setId(groupId);

                // servers: list
                Object serversObj = map.get("servers");
                if (serversObj instanceof List) {
                    List<String> servers = new ArrayList<>();
                    for (Object o : (List<Object>) serversObj) {
                        if (o != null) servers.add(o.toString());
                    }
                    sg.setServers(servers);
                    for (String s : servers) {
                        serverToGroups.computeIfAbsent(s.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(groupId.toLowerCase(Locale.ROOT));
                    }
                }

                // chat
                Object chatObj = map.get("chat");
                if (chatObj != null) sg.setChat(chatObj.toString());

                // events
                Object eventsObj = map.get("events");
                if (eventsObj != null) sg.setEvents(eventsObj.toString());

                // join: can be a map with message or a scalar like 'custom'/'vanilla'
                Object joinObj = map.get("join");
                if (joinObj instanceof Map) {
                    Object msg = ((Map<String, Object>) joinObj).get("message");
                    if (msg != null) sg.setJoinMessage(msg.toString());
                } else if (joinObj instanceof String) {
                    if ("custom".equalsIgnoreCase((String) joinObj)) {
                        // leave joinMessage null but events/custom can be used
                    }
                }
                // top-level message fallback
                Object joinMsgTop = map.get("message");
                if (joinMsgTop != null && sg.getJoinMessage() == null) sg.setJoinMessage(joinMsgTop.toString());

                // leave
                Object leaveObj = map.get("leave");
                if (leaveObj instanceof Map) {
                    Object msg = ((Map<String, Object>) leaveObj).get("message");
                    if (msg != null) sg.setLeaveMessage(msg.toString());
                } else if (leaveObj instanceof String) {
                    // leave: vanilla or custom - handled by events or presence of message
                }

                // senders (virtual)
                Object sendersObj = map.get("senders");
                if (sendersObj instanceof List) {
                    List<String> senders = new ArrayList<>();
                    for (Object o : (List<Object>) sendersObj) if (o != null) senders.add(o.toString());
                    sg.setSenders(senders);
                }
                // discord flag: optional boolean (default true)
                Object discordObj = map.get("discord");
                if (discordObj != null) {
                    if (discordObj instanceof Boolean) sg.setDiscord((Boolean) discordObj);
                    else sg.setDiscord(Boolean.parseBoolean(discordObj.toString()));
                }

                rules.put(groupId.toLowerCase(Locale.ROOT), sg);
            }

            // Store serverToGroups map into the rules map as a special key mapping if needed
            // For ease of access we attach a lightweight mapping field via a temporary map stored in rules under key "__server_map" - but better to store as a separate field.
            this.serverToGroups = serverToGroups;

            logger.info("Loaded {} server group rules and {} server->group mappings", rules.size(), serverToGroups.size());
        } catch (Exception e) {
            logger.error("Failed to load server_groups.yml", e);
        }
    }

    public synchronized boolean reload() {
        try {
            load();
            return true;
        } catch (Exception e) {
            logger.error("Failed to reload server groups", e);
            return false;
        }
    }

    /**
     * Decide whether a message originating from `senderServer` should be delivered
     * to a player on `recipientServer` for the given message type.
     *
     * Current simple rules:
     * - If there is no rule for the sender, return true.
     * - If rule.enabled == false, return true.
     * - If rule.type == whitelist -> deliver only if recipient in list.
     * - If rule.type == blacklist -> deliver unless recipient in list.
     */
    public boolean shouldDeliver(String senderServer, String recipientServer, MessageType type) {
        if (senderServer == null) senderServer = "";
        if (recipientServer == null) recipientServer = "";

        String senderKey = senderServer.toLowerCase(Locale.ROOT);
        String recipientKey = recipientServer.toLowerCase(Locale.ROOT);

        // 1) If sender is a virtual sender (e.g., DISCORD), check groups that list this sender
        boolean foundSenderGroup = false;
        for (Map.Entry<String, ServerGroup> e : rules.entrySet()) {
            ServerGroup sg = e.getValue();
            if (sg.getSenders() != null) {
                for (String s : sg.getSenders()) {
                    if (s != null && s.equalsIgnoreCase(senderServer)) {
                        foundSenderGroup = true;
                        // deliver only to recipients that are in this group's servers
                        List<String> servers = sg.getServers();
                        if (servers != null) {
                            for (String rs : servers) if (rs != null && rs.equalsIgnoreCase(recipientServer)) return true;
                        }
                    }
                }
            }
        }
        if (foundSenderGroup) {
            // Sender is mapped to one or more groups via `senders`, but none of those
            // groups contained the recipient — therefore delivery should be denied.
            return false;
        }

        // 2) If sender maps to one or more groups, apply group semantics (multi-group OR semantics)
        List<String> senderGroups = getGroupsForServer(senderServer);
        if (!senderGroups.isEmpty()) {
            for (String gid : senderGroups) {
                ServerGroup sg = rules.get(gid.toLowerCase(Locale.ROOT));
                if (sg == null) continue;

                // CHAT handling
                if (type == MessageType.CHAT) {
                    String chatType = sg.getChat();
                    if (chatType != null && chatType.equalsIgnoreCase("local")) {
                        // local: do not forward to other servers
                        if (!recipientKey.equals(senderKey)) continue;
                    } else {
                        // global (or unspecified) -> deliver if recipient shares the group
                        List<String> recipients = sg.getServers();
                        if (recipients != null) {
                            for (String rs : recipients) if (rs != null && rs.equalsIgnoreCase(recipientServer)) return true;
                        }
                    }
                }

                // JOIN / LEAVE handling
                if (type == MessageType.JOIN || type == MessageType.LEAVE) {
                    String events = sg.getEvents();
                    boolean hasCustom = (type == MessageType.JOIN ? sg.getJoinMessage() != null : sg.getLeaveMessage() != null);
                    if ((events != null && events.equalsIgnoreCase("custom")) || hasCustom) {
                        List<String> recipients = sg.getServers();
                        if (recipients != null) {
                            for (String rs : recipients) if (rs != null && rs.equalsIgnoreCase(recipientServer)) return true;
                        }
                    } else {
                        // events == vanilla -> plugin should not send custom messages
                        continue;
                    }
                }

                // DISCORD handling - treat like chat for groups that include DISCORD as sender via `senders` or fall back to chat
                if (type == MessageType.DISCORD) {
                    List<String> recipients = sg.getServers();
                    if (recipients != null) {
                        for (String rs : recipients) if (rs != null && rs.equalsIgnoreCase(recipientServer)) return true;
                    }
                }
            }
            // No group allowed delivery
            return false;
        }

        // 3) Fallback to legacy per-server rules: if a rule exists keyed by the sender server name
        ServerGroup legacy = rules.get(senderKey);
        if (legacy != null && legacy.getList() != null) {
            if (!legacy.isEnabled()) return true;
            List<String> list = legacy.getList();
            final String rkey = recipientServer;
            boolean recipientInList = list.stream().filter(Objects::nonNull).anyMatch(s -> s.equalsIgnoreCase(rkey));
            String typeStr = legacy.getType() != null ? legacy.getType().toLowerCase(Locale.ROOT) : "whitelist";
            if ("whitelist".equals(typeStr)) return recipientInList;
            if ("blacklist".equals(typeStr)) return !recipientInList;
        }

        // Default allow
        return true;
    }

}
