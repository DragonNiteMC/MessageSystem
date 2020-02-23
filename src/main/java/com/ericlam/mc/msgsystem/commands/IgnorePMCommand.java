package com.ericlam.mc.msgsystem.commands;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.container.OfflinePlayer;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class IgnorePMCommand extends MSGSystemCommandNode {

    public IgnorePMCommand() {
        super(null, "msgignore", null, "忽略玩家私訊", "[player]", "mignore");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        if (list.size() < 1) {

            List<String> ignoredPlayers = playerIgnoreManager.getIgnoredPlayers(player.getUniqueId()).stream().map(OfflinePlayer::getName).collect(Collectors.toList());
            MessageBuilder.sendMessage(player, msg.get(ignoredPlayers.size() > 0 ? "msg.ignore.list" : "msg.ignore.none").replace("<player>", player.getDisplayName()).replace("<list>", ignoredPlayers.toString()));

        } else {
            String name = list.get(0);
            if (name.equals(player.getName())) {
                MessageBuilder.sendMessage(player, msg.get("msg.send-self"));
                return;
            }
            HyperNiteMC.getAPI().getPlayerManager().getOfflinePlayer(name).whenComplete(((offlinePlayer, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    MessageBuilder.sendMessage(player, ChatColor.RED + throwable.getMessage());
                    return;
                }

                if (offlinePlayer.isEmpty()) {
                    MessageBuilder.sendMessage(player, HyperNiteMC.getAPI().getMainConfig().getNoThisPlayer());
                    return;
                }

                OfflinePlayer target = offlinePlayer.get();


                boolean isIgnored = playerIgnoreManager.isIgnoredPlayer(player, target.getUniqueId());
                String path = "msg.ignore.";
                if (isIgnored) {
                    playerIgnoreManager.removeIgnoredPlayer(player, target);
                    path = path.concat("no-ignored");
                } else {
                    playerIgnoreManager.addIgnorePlayer(player, target);
                    path = path.concat("ignored");
                }

                MessageBuilder.sendMessage(player, msg.get(path).replace("<ignored>", target.getName()));


            }));
        }

    }
}
