package com.ericlam.mc.msgsystem.main.commands;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.config.ConfigManager;
import com.ericlam.mc.bungee.hnmc.permission.Perm;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import net.md_5.bungee.api.CommandSender;

import java.util.List;

public class ReloadCommand extends CommandNode {

    private ConfigManager configManager;

    public ReloadCommand() {
        super(null, "msgreload", Perm.ADMIN, "重載", null, "mreload", "msgsystemreload");
        this.configManager = MSGSystem.getApi().getConfigManager();
    }

    @Override
    public void executeCommand(CommandSender commandSender, List<String> list) {
        configManager.reloadAllConfigs();
        MessageBuilder.sendMessage(commandSender, configManager.getMessage("msg.reloaded"));
    }

    @Override
    public List<String> executeTabCompletion(CommandSender commandSender, List<String> list) {
        return null;
    }
}
