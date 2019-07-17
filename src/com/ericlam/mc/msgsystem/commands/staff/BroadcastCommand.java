package com.ericlam.mc.msgsystem.commands.staff;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import com.ericlam.mc.msgsystem.commands.MSGSystemCommandNode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;

public class BroadcastCommand extends MSGSystemCommandNode {
    public BroadcastCommand(CommandNode parent) {
        super(parent, "msgbroadcast", Perm.MOD, "公告指令", "<messages>", "bc", "broadcast", "msgbc", "shout");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        final String msg = ChatColor.translateAlternateColorCodes('&', String.join(" ", list));
        String[] bc = Arrays.stream(configManager.getMessageList("broadcast", false)).map(l -> l.concat("§r").replace("<msg>", msg).replace("<sender>", player.getDisplayName())).toArray(String[]::new);
        ProxyServer.getInstance().getPlayers().forEach(p -> new MessageBuilder(bc).sendPlayer(p));
    }

}
