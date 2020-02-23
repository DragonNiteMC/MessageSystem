package com.ericlam.mc.msgsystem.commands;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import com.ericlam.mc.msgsystem.config.MSGConfig;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import net.md_5.bungee.api.CommandSender;

import java.util.List;

public class ReloadCommand extends CommandNode {

    private MSGConfig msg;

    public ReloadCommand() {
        super(null, "msgreload", Perm.ADMIN, "重載", null, "mreload", "msgsystemreload");
        this.msg = MSGSystem.getApi().getConfigManager().getConfigAs(MSGConfig.class);
    }

    @Override
    public void executeCommand(CommandSender commandSender, List<String> list) {
        MSGSystem.getApi().pluginReload();
        MessageBuilder.sendMessage(commandSender, msg.get("msg.reloaded"));
    }

    @Override
    public List<String> executeTabCompletion(CommandSender commandSender, List<String> list) {
        return null;
    }
}
