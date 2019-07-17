package com.ericlam.mc.msgsystem.commands.staff.announce;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import com.ericlam.mc.msgsystem.commands.MSGSystemCommandNode;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class AnnounceSendCommand extends MSGSystemCommandNode {
    public AnnounceSendCommand(CommandNode parent) {
        super(parent, "send", Perm.ADMIN, "發送公告訊息", "<key> <sectionKey>", "broadcast");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        boolean sent = announceManager.sendAnnouncement(list.get(0), list.get(1));
        final String path = sent ? "msg.announce.sent" : "msg.announce.none";
        MessageBuilder.sendMessage(player, configManager.getMessage(path));
    }
}
