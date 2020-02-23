package com.ericlam.mc.msgsystem.api;

import com.ericlam.mc.msgsystem.config.Announcer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AnnounceManager {

    List<ServerInfo> getAnnounceServers(String key);

    List<String> getMessageList(String key, String sectionKey);

    Optional<Announcer> getAnnouncer(String key);

    boolean sendAnnouncement(String key, String sectionKey);

    Set<String> getMessagesSectionKeys(String key);

    Map<String, Announcer> getAnnouncerMap();

}
