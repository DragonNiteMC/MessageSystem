package com.ericlam.mc.msgsystem.commands.staff.announce;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import com.ericlam.mc.msgsystem.commands.MSGSystemCommandNode;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class AnnounceCheckCommand extends MSGSystemCommandNode {
    public AnnounceCheckCommand(CommandNode parent) {
        super(parent, "check", Perm.ADMIN, "檢查公告訊息", "<key> <sectionKey>", "see");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        String[] arr = announceManager.getMessageList(list.get(0), list.get(1)).toArray(String[]::new);
        if (arr.length > 0) {
            new MessageBuilder(msg.get("msg.announce.list").replace("<key>", list.get(0)).replace("<sectionKey>", list.get(1))).nextLine().add(arr).sendPlayer(player);
        } else {
            MessageBuilder.sendMessage(player, msg.get("msg.announce.none"));
        }
    }
}
