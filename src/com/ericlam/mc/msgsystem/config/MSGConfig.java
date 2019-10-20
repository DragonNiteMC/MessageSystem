package com.ericlam.mc.msgsystem.config;

import com.ericlam.mc.bungee.hnmc.config.Prop;
import com.ericlam.mc.bungee.hnmc.config.yaml.MessageConfiguration;
import com.ericlam.mc.bungee.hnmc.config.yaml.Prefix;
import com.ericlam.mc.bungee.hnmc.config.yaml.Resource;

import java.util.List;
import java.util.Map;

@Resource(locate = "config.yml")
@Prefix(path = "prefix")
public class MSGConfig extends MessageConfiguration {

    @Prop(path = "player-info")
    public List<String> playerInfo;

    @Prop
    public List<String> broadcast;

    @Prop(path = "msg.announce.section-keys")
    public List<String> sectionKey;

    @Prop(path = "server-alias")
    public Map<String, String> serverAlias;

    @Prop(path = "group-alias")
    public Map<String, String> groupAlias;

    @Prop(path = "channels")
    public Map<String, String> channelMap;
}
