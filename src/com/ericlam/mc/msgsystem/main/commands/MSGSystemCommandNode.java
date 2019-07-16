package com.ericlam.mc.msgsystem.main.commands;

import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.hnmc.config.ConfigManager;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import com.ericlam.mc.msgsystem.main.api.PMManager;
import com.ericlam.mc.msgsystem.main.api.PlayerIgnoreManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public abstract class MSGSystemCommandNode extends CommandNode {

    protected ConfigManager configManager;

    protected PlayerIgnoreManager playerIgnoreManager;

    protected PMManager pmManager;

    public MSGSystemCommandNode(CommandNode parent, String command, String permission, String description, String placeholder, String... alias) {
        super(parent, command, permission, description, placeholder, alias);
        this.configManager = MSGSystem.getApi().getConfigManager();
        this.playerIgnoreManager = MSGSystem.getApi().getPlayerIgnoreManager();
        this.pmManager = MSGSystem.getApi().getPMManager();
    }

    @Override
    public void executeCommand(CommandSender commandSender, List<String> list) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            MessageBuilder.sendMessage(commandSender, HyperNiteMC.getAPI().getMainConfig().getNotPlayer());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        this.executionPlayer(player, list);
    }

    public abstract void executionPlayer(ProxiedPlayer player, List<String> list);
}
