package com.ericlam.mc.msgsystem.manager;

import com.ericlam.mc.msgsystem.api.ChatSpyManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class ChatSpyPlayerManager implements ChatSpyManager {

    private final Set<UUID> spyer = new HashSet<>();

    @Override
    public void addChatSpyer(UUID uuid) {
        this.spyer.add(uuid);
    }

    @Override
    public void removeChatSpyer(UUID uuid) {
        this.spyer.remove(uuid);
    }

    @Override
    public boolean isChatSpyer(UUID uuid) {
        return this.spyer.contains(uuid);
    }

    @Override
    public List<ProxiedPlayer> getOnlineChatSpyers() {
        return this.spyer.stream().map(uuid -> ProxyServer.getInstance().getPlayer(uuid)).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
