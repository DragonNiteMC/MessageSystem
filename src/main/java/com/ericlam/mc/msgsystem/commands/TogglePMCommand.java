package com.ericlam.mc.msgsystem.commands;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class TogglePMCommand extends MSGSystemCommandNode {

    public TogglePMCommand() {
        super(null, "msgtoggle", null, "切換私訊系統開關", "[player]", "mtoggle");
    }

    @Override
    public void executionPlayer(ProxiedPlayer player, List<String> list) {
        if (list.size() < 1) {
            final boolean previousToggle = pmManager.isDisabledPM(player);
            pmManager.setDisabledPM(player, !previousToggle);
            final String path = "msg.toggle.".concat(pmManager.isDisabledPM(player) ? "disable" : "enable");
            MessageBuilder.sendMessage(player, msg.get(path));
        } else {
            if (!player.hasPermission(Perm.MOD)) {
                MessageBuilder.sendMessage(player, HyperNiteMC.getAPI().getMainConfig().getNoPermission());
                return;
            }
            String name = list.get(0);
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);
            if (target == null) {
                MessageBuilder.sendMessage(player, HyperNiteMC.getAPI().getMainConfig().getNoThisPlayer());
                return;
            }
            final boolean previousToggle = pmManager.isDisabledPM(target);
            pmManager.setDisabledPM(target, !previousToggle);
            final String path = "msg.toggle.".concat(pmManager.isDisabledPM(target) ? "disable-other" : "enable-other");
            final String targetPath = "msg.toggle.be-".concat(pmManager.isDisabledPM(target) ? "disable" : "enable");
            MessageBuilder.sendMessage(player, msg.get(path).replace("<toggled>", target.getDisplayName()));
            MessageBuilder.sendMessage(target, msg.get(targetPath).replace("<mod>", player.getDisplayName()));
        }
    }
}
