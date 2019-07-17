package com.ericlam.mc.msgsystem;

import com.ericlam.mc.bungee.hnmc.config.ConfigSetter;
import com.ericlam.mc.bungee.hnmc.config.Extract;
import com.ericlam.mc.msgsystem.container.Announcer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.stream.Collectors;

public class MSGConfig extends ConfigSetter {

    @Extract(name = "sa")
    private Map<String, String> serverAlias = new HashMap<>();


    @Extract(name = "am")
    private Map<String, Announcer> announcerMap = new HashMap<>();

    @Extract(name = "cmd-cooldown")
    private long cmdCooldown;
    @Extract(name = "duplicate-max")
    private int duplicateMax;
    @Extract(name = "cooldown-chat")
    private long cooldownChat;
    @Extract(name = "whitelist-ip")
    private List<String> whitelistIP;
    @Extract(name = "whitelist-domain")
    private List<String> whitelistDomains;

    public MSGConfig(Plugin plugin) {
        super(plugin,
                "config.yml", "announce.yml", "chat.yml");
    }

    @Override
    public void loadConfig(HashMap<String, Configuration> hashMap) {
        Configuration format = hashMap.get("config.yml");
        this.cmdCooldown = format.getLong("command-cooldown");
        this.serverAlias.clear();
        Configuration aliasSection = format.getSection("server-alias");
        for (String key : aliasSection.getKeys()) {
            String display = ChatColor.translateAlternateColorCodes('&', aliasSection.getString(key));
            this.serverAlias.put(key, display);
        }
        this.announcerMap.clear();
        Configuration announce = hashMap.get("announce.yml");
        for (String key : announce.getKeys()) {
            List<String> serverNames;
            String servers = announce.getString(key + ".servers");
            if (servers.contentEquals("[ALL]") || servers.isBlank()) {
                serverNames = null;
            } else {
                serverNames = Arrays.stream(servers.split("\\|")).distinct().collect(Collectors.toList());
            }
            long delay = announce.getLong(key + ".delay");
            Configuration messagesSection = announce.getSection(key + ".messages");
            Map<String, List<String>> messages = new LinkedHashMap<>();
            for (String sectionKey : messagesSection.getKeys()) {
                List<String> messagelist = messagesSection.getStringList(sectionKey).stream().map(l -> l.concat("&r")).collect(Collectors.toList());
                messages.put(sectionKey, messagelist);
            }
            Announcer announcer = new Announcer(messages, serverNames, delay);
            this.announcerMap.put(key, announcer);
        }
        Configuration chat = hashMap.get("chat.yml");
        this.duplicateMax = chat.getInt("duplicate-max");
        this.cooldownChat = chat.getLong("cooldown-chat");
        this.whitelistIP = chat.getStringList("whitelist.ips");
        this.whitelistDomains = chat.getStringList("whitelist.domains");
    }
}
