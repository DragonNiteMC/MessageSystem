package com.ericlam.mc.msgsystem.config;

import com.ericlam.mc.bungee.hnmc.config.yaml.BungeeConfiguration;
import com.ericlam.mc.bungee.hnmc.config.yaml.Resource;

import java.util.Map;

@Resource(locate = "announce.yml")
public class AnnounceConfig extends BungeeConfiguration {

    private Map<String, Announcer> announces;

    public Map<String, Announcer> getAnnouncerMap() {
        return announces;
    }
}
