package com.ericlam.mc.msgsystem.config;

import com.ericlam.mc.bungee.dnmc.config.yaml.MessageConfiguration;
import com.ericlam.mc.bungee.dnmc.config.yaml.Prefix;
import com.ericlam.mc.bungee.dnmc.config.yaml.Resource;

import java.util.List;
import java.util.Map;

@Resource(locate = "config.yml")
@Prefix(path = "prefix")
public class MSGConfig extends MessageConfiguration {

    public Map<String, String> groupAlias;
    public Map<String, String> serverAlias;
    public Map<String, String> channels;
    public List<String> playerInfo;
    public List<String> broadcast;
}
