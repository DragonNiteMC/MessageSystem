package com.ericlam.mc.msgsystem.commands.staff.announce;

import com.ericlam.mc.bungee.dnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.dnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.dnmc.permission.Perm;
import com.ericlam.mc.msgsystem.commands.MSGSystemCommandNode;
import com.ericlam.mc.msgsystem.config.Announcer;
import com.ericlam.mc.msgsystem.config.MSGConfig;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Optional;

public class AnnounceListCommand extends MSGSystemCommandNode {
    public AnnounceListCommand(CommandNode parent) {
        super(parent, "list", Perm.ADMIN, "查看公告列表", "[key]", "info");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        if (list.size() < 1) {
            String keys = announceManager.getAnnouncerMap().keySet().toString();
            new MessageBuilder(msg.get("msg.announce.keys").replace("<list>", keys)).sendPlayer(player);
        } else {
            String key = list.get(0);
            Optional<Announcer> announcerOptional = announceManager.getAnnouncer(key);
            announcerOptional.ifPresentOrElse(announcer -> {
                String sectionKeys = announcer.getMessages().keySet().toString();
                String[] info = configManager.getConfigAs(MSGConfig.class).getList("msg.announce.section-keys").stream()
                        .map(line -> line
                                .replace("<key>", key)
                                .replace("<sec>", announcer.getDelay() + "")
                                .replace("<list>", sectionKeys))
                        .toArray(String[]::new);
                new MessageBuilder(info).sendPlayer(player);
            }, () -> MessageBuilder.sendMessage(player, msg.get("msg.announce.none")));
        }
    }
}
