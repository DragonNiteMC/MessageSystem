package com.ericlam.mc.msgsystem.api;

import com.ericlam.mc.bungee.dnmc.container.OfflinePlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public interface PlayerIgnoreManager extends UserLoadable {

    List<OfflinePlayer> getIgnoredPlayers(UUID uuid);

    boolean isIgnoredPlayer(ProxiedPlayer player, UUID target);

    void addIgnorePlayer(ProxiedPlayer player, OfflinePlayer target);

    void removeIgnoredPlayer(ProxiedPlayer player, OfflinePlayer target);
}
