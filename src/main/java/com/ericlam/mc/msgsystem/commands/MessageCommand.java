package com.ericlam.mc.msgsystem.commands;

import com.ericlam.mc.bungee.hnmc.permission.Perm;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class MessageCommand extends MSGSystemCommandNode {

    public MessageCommand() {
        super(null, "msg", null, "發送私人訊息", "<player> <message>", "message", "m", "whisper", "tell", "t", "pm");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        final String name = list.remove(0);
        final String msg = String.join(" ", list);
        final String colored = player.hasPermission(Perm.DONOR) ? ChatColor.translateAlternateColorCodes('&', msg) : msg;
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);
        pmManager.sendPrivateMessage(player, target, colored);
    }
}
