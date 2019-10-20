package com.ericlam.mc.msgsystem.config;

import com.ericlam.mc.bungee.hnmc.config.Prop;
import com.ericlam.mc.bungee.hnmc.config.yaml.BungeeConfiguration;
import com.ericlam.mc.bungee.hnmc.config.yaml.Resource;

import java.util.List;
import java.util.Map;

@Resource(locate = "chat.yml")
public class ChatConfig extends BungeeConfiguration {

    @Prop(path = "duplicate-max")
    public int duplicateMax;

    @Prop(path = "cooldown-chat")
    public long cooldownChat;

    @Prop(path = "anti-spam-duplicate")
    public int antiSpamDuplicate;

    @Prop(path = "spam-char-max")
    public int spamCharMax;

    @Prop(path = "command-cooldown")
    public long commandCooldown;

    @Prop
    public Map<String, List<String>> whitelist;
}
