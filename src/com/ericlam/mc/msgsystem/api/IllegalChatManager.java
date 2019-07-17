package com.ericlam.mc.msgsystem.api;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;

public interface IllegalChatManager {

    boolean antiSpam(ProxiedPlayer player, final ChatEvent e);

    boolean antiAdvertise(ProxiedPlayer player, final ChatEvent e);

    boolean antiDuplicate(ProxiedPlayer player, final ChatEvent e);
}
