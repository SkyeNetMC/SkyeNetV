package me.pilkeysek.skyenetv.config;

import java.util.List;

public class ServerGroup {
    private String id;
    private boolean enabled = true;
    // legacy fields (kept for backward compatibility)
    private String type = "whitelist"; // whitelist | blacklist
    private List<String> list;

    // New group-based fields
    private List<String> servers;
    private String chat; // global | local
    private String events; // custom | vanilla
    private String joinMessage;
    private String leaveMessage;
    private List<String> senders; // optional virtual senders like DISCORD
    private boolean discord = true; // whether this group's servers sync to Discord

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public List<String> getSenders() {
        return senders;
    }

    public void setSenders(List<String> senders) {
        this.senders = senders;
    }

    public boolean isDiscord() {
        return discord;
    }

    public void setDiscord(boolean discord) {
        this.discord = discord;
    }
}
