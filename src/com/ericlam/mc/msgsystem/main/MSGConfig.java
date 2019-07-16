package com.ericlam.mc.msgsystem.main;

import com.ericlam.mc.bungee.hnmc.config.ConfigSetter;
import com.ericlam.mc.bungee.hnmc.config.Extract;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class MSGConfig extends ConfigSetter {

    @Extract(name = "sa")
    private Map<String, String> serverAlias = new HashMap<>();


    public MSGConfig(Plugin plugin) {
        super(plugin, "format.yml");
    }

    @Override
    public void loadConfig(HashMap<String, Configuration> hashMap) {
        Configuration format = hashMap.get("format.yml");
        this.serverAlias.clear();
        Configuration aliasSection = format.getSection("server-alias");
        for (String key : aliasSection.getKeys()) {
            String display = ChatColor.translateAlternateColorCodes('&', aliasSection.getString(key));
            this.serverAlias.put(key, display);
        }
    }
}
