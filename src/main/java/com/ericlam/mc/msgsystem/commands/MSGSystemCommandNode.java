package com.ericlam.mc.msgsystem.commands;

import com.ericlam.mc.bungee.dnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.dnmc.commands.caxerx.CommandNode;
import com.ericlam.mc.bungee.dnmc.config.YamlManager;
import com.ericlam.mc.bungee.dnmc.main.DragoniteMC;
import com.ericlam.mc.msgsystem.api.AnnounceManager;
import com.ericlam.mc.msgsystem.api.ChatSpyManager;
import com.ericlam.mc.msgsystem.api.PMManager;
import com.ericlam.mc.msgsystem.api.PlayerIgnoreManager;
import com.ericlam.mc.msgsystem.config.ChatConfig;
import com.ericlam.mc.msgsystem.config.MSGConfig;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class MSGSystemCommandNode extends CommandNode {

    private static Map<UUID, LocalDateTime> lastCommandExecute = new ConcurrentHashMap<>();

    protected YamlManager configManager;

    protected MSGConfig msg;

    protected PlayerIgnoreManager playerIgnoreManager;

    protected PMManager pmManager;

    protected ChatSpyManager chatSpyManager;

    protected AnnounceManager announceManager;

    public MSGSystemCommandNode(CommandNode parent, String command, String permission, String description, String placeholder, String... alias) {
        super(parent, command, permission, description, placeholder, alias);
        this.configManager = MSGSystem.getApi().getConfigManager();
        this.msg = configManager.getConfigAs(MSGConfig.class);
        this.playerIgnoreManager = MSGSystem.getApi().getPlayerIgnoreManager();
        this.pmManager = MSGSystem.getApi().getPMManager();
        this.chatSpyManager = MSGSystem.getApi().getChatSpyManager();
        this.announceManager = MSGSystem.getApi().getAnnounceManager();
    }

    @Override
    public void executeCommand(CommandSender commandSender, List<String> list) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            MessageBuilder.sendMessage(commandSender, DragoniteMC.getAPI().getMainConfig().getNotPlayer());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (lastCommandExecute.containsKey(player.getUniqueId())) {
            long cooldown = configManager.getConfigAs(ChatConfig.class).commandCooldown;
            Duration duration = Duration.between(lastCommandExecute.get(player.getUniqueId()), LocalDateTime.now());
            if (duration.toMillis() < cooldown) {
                double sec = BigDecimal.valueOf((double) (cooldown - duration.toMillis()) / 1000).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
                MessageBuilder.sendMessage(player, msg.get("msg.too-fast").replace("<sec>", sec + ""));
                return;
            }
        }
        this.executionPlayer(player, list);
        lastCommandExecute.put(player.getUniqueId(), LocalDateTime.now());
    }

    @Override
    public List<String> executeTabCompletion(CommandSender commandSender, List<String> list) {
        if (this.getPlaceholder() == null) return null;
        String[] placeholders = this.getPlaceholder().split(" ");
        List<Integer> integers = new LinkedList<>();
        for (int i = 0; i < placeholders.length; i++) {
            if (!placeholders[i].contains("player")) continue;
            integers.add(i);

        }
        return integers.contains(list.size() - 1) ? ProxyServer.getInstance().getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList()) : null;
    }

    public abstract void executionPlayer(ProxiedPlayer player, List<String> list);
}
