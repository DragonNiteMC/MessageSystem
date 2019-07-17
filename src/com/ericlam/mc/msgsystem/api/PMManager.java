package com.ericlam.mc.msgsystem.api;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PMManager extends UserLoadable {

    Optional<ProxiedPlayer> getLastMessager(UUID uuid);

    void changeLastMessager(ProxiedPlayer player, ProxiedPlayer target);

    void sendPrivateMessage(ProxiedPlayer player, ProxiedPlayer target, String message);

    TextComponent getPrivateMessage(ProxiedPlayer player, ProxiedPlayer target, String message);

    String[] getInfo(ProxiedPlayer player);

    boolean isDisabledPM(ProxiedPlayer player);

    void setDisabledPM(ProxiedPlayer player, boolean disable);

    Map<String, String> getDisplayAlias();

}
