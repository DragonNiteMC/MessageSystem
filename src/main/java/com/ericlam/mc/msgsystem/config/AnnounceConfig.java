package com.ericlam.mc.msgsystem.config;

import com.ericlam.mc.bungee.hnmc.config.yaml.BungeeConfiguration;
import com.ericlam.mc.bungee.hnmc.config.yaml.Resource;

import java.util.Map;

@Resource(locate = "announce.yml")
public class AnnounceConfig extends BungeeConfiguration {

    public Map<String, Announcer> announces;

}
