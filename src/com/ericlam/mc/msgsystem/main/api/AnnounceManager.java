package com.ericlam.mc.msgsystem.main.api;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public interface AnnounceManager {

    List<ServerInfo> getAnnounceServers(String key);

    List<String> getMessageList(String key);

    
}
