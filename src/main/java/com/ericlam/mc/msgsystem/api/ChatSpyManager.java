package com.ericlam.mc.msgsystem.api;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public interface ChatSpyManager {

    void addChatSpyer(UUID uuid);

    void removeChatSpyer(UUID uuid);

    boolean isChatSpyer(UUID uuid);

    List<ProxiedPlayer> getOnlineChatSpyers();

}
