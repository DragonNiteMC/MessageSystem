package com.ericlam.mc.msgsystem.config;

import com.ericlam.mc.bungee.hnmc.config.yaml.BungeeConfiguration;
import com.ericlam.mc.bungee.hnmc.config.yaml.Resource;

import java.util.List;
import java.util.Map;

@Resource(locate = "chat.yml")
public class ChatConfig extends BungeeConfiguration {

    public int duplicateMax;

    public long cooldownChat;

    public int antiSpamDuplicate;

    public int spamCharMax;

    public long commandCooldown;

    public Map<String, List<String>> whitelist;
}
