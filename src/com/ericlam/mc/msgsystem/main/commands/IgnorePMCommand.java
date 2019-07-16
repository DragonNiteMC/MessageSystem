package com.ericlam.mc.msgsystem.main.commands;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.container.OfflinePlayer;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
            MessageBuilder.sendMessage(player, configManager.getMessage(ignoredPlayers.size() > 1 ? "msg.ignore.list" : "msg.ignore.none").replace("<player>", player.getDisplayName()).replace("<list>", ignoredPlayers.toString()));

        } else {
            String name = list.get(0);
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
                String path = "ignore.";
                if (isIgnored) {
                    playerIgnoreManager.removeIgnoredPlayer(player, target);
                    path = path.concat("no-ignored");
                } else {
                    playerIgnoreManager.addIgnorePlayer(player, target);
                    path = path.concat("ignored");
                }

                MessageBuilder.sendMessage(player, configManager.getMessage(path).replace("<ignored>", target.getName()));


            }));
        }

    }

    @Override
    public List<String> executeTabCompletion(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? ProxyServer.getInstance().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toList()) : null;
    }
}
