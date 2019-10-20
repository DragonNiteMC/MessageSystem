package com.ericlam.mc.msgsystem.config;

import com.ericlam.mc.bungee.hnmc.config.Prop;
import com.ericlam.mc.bungee.hnmc.config.yaml.BungeeConfiguration;
import com.ericlam.mc.bungee.hnmc.config.yaml.Resource;
import com.ericlam.mc.msgsystem.container.Announcer;

import java.util.Map;

@Resource(locate = "announce.yml")
public class AnnounceConfig extends BungeeConfiguration {

    @Prop(path = "*")
    private Map<String, Announcer> announcerMap;

    public Map<String, Announcer> getAnnouncerMap() {
        return announcerMap;
    }
}
