package com.ericlam.mc.msgsystem.commands.staff;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import com.ericlam.mc.msgsystem.commands.MSGSystemCommandNode;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public class ChatSpyCommand extends MSGSystemCommandNode {

    public ChatSpyCommand(CommandNode parent) {
        super(parent, "chatspy", Perm.HELPER, "私訊監察模式", null, "msgspy", "msgchatspy");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        final UUID uuid = player.getUniqueId();

        String path;

        if (chatSpyManager.isChatSpyer(uuid)) {

            chatSpyManager.removeChatSpyer(uuid);
            path = "msg.spy.disable";

        } else {

            chatSpyManager.addChatSpyer(uuid);
            path = "msg.spy.enable";

        }

        MessageBuilder.sendMessage(player, configManager.getMessage(path));
    }
}
