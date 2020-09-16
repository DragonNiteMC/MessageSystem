package com.ericlam.mc.msgsystem.manager;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.msgsystem.api.AnnounceManager;
import com.ericlam.mc.msgsystem.config.AnnounceConfig;
import com.ericlam.mc.msgsystem.config.Announcer;
import com.ericlam.mc.msgsystem.config.MSGConfig;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class AnnouncementManager implements AnnounceManager {


    private Map<String, Announcer> announceMap;
    private final AnnounceConfig announceConfig;
    private String suggestSendMsg;
    private final MSGConfig msg;

    public AnnouncementManager() {
        this.announceConfig = MSGSystem.getApi().getConfigManager().getConfigAs(AnnounceConfig.class);
        this.msg = MSGSystem.getApi().getConfigManager().getConfigAs(MSGConfig.class);
        this.announceMap = announceConfig.announces;
        this.suggestSendMsg = msg.get("suggest-send");
    }

    public void reloadAnnouncer() {
        this.announceMap = announceConfig.announces;
        this.suggestSendMsg = msg.get("suggest-send");
    }


    @Override
    public List<ServerInfo> getAnnounceServers(String key) {
        if (!announceMap.containsKey(key)) return List.of();
        List<String> serverInfos = announceMap.get(key).getServerInfos();
        return serverInfos == null ? new ArrayList<>(ProxyServer.getInstance().getServers().values()) : serverInfos.stream().map(s -> ProxyServer.getInstance().getServerInfo(s)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<String> getMessageList(String key, String sectionKey) {
        return Optional.ofNullable(announceMap.get(key)).map(Announcer::getMessages).map(map -> map.get(sectionKey)).orElse(List.of());
    }

    @Override
    public Optional<Announcer> getAnnouncer(String key) {
        return Optional.ofNullable(this.announceMap.get(key));
    }

    @Override
    public boolean sendAnnouncement(String key, String sectionKey) {
        if (!announceMap.containsKey(key)) return false;
        Announcer announcer = announceMap.get(key);
        Map<String, List<String>> listMap = announcer.getMessages();
        if (!listMap.containsKey(sectionKey)) return false;
        List<String> messsages = listMap.get(sectionKey);
        List<BaseComponent[]> components = messsages.stream().map(line -> {
            String[] teleport = line.split("<\\|>");
            if (teleport.length < 2) return new MessageBuilder(teleport[0]).build();
            final String msg = teleport[1].replaceAll("'", "").replaceAll("\"", "");
            final String server = teleport[0];
            ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
            if (info == null) return new MessageBuilder(teleport[0]).build();
            String name = MSGSystem.getApi().getPMManager().getDisplayAlias().get(info.getName());
            if (name == null) name = info.getName();
            return new MessageBuilder(msg).hoverText(suggestSendMsg.replace("<server>", name)).run(proxiedPlayer -> proxiedPlayer.connect(info)).build();
        }).collect(Collectors.toList());
        List<String> infos = announcer.getServerInfos();
        Collection<ProxiedPlayer> playersToSend;
        if (infos == null) {
            playersToSend = ProxyServer.getInstance().getPlayers();
        } else {
            playersToSend = new ArrayList<>();
            infos.stream().map(s -> ProxyServer.getInstance().getServerInfo(s)).filter(Objects::nonNull).map(ServerInfo::getPlayers).forEach(playersToSend::addAll);
        }
        playersToSend.forEach(p -> components.forEach(p::sendMessage));
        return true;
    }

    @Override
    public Set<String> getMessagesSectionKeys(String key) {
        return Optional.ofNullable(this.announceMap.get(key)).map(announcer -> announcer.getMessages().keySet()).orElse(Set.of());
    }

    @Override
    public Map<String, Announcer> getAnnouncerMap() {
        return this.announceMap;
    }

}
