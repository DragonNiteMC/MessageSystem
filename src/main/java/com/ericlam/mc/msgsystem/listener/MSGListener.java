package com.ericlam.mc.msgsystem.listener;

import com.ericlam.mc.bungee.hnmc.builders.AdvMessageBuilder;
import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.config.YamlManager;
import com.ericlam.mc.msgsystem.api.ChannelManager;
import com.ericlam.mc.msgsystem.api.ChatSpyManager;
import com.ericlam.mc.msgsystem.api.PMManager;
import com.ericlam.mc.msgsystem.api.PlayerIgnoreManager;
import com.ericlam.mc.msgsystem.config.MSGConfig;
import com.ericlam.mc.msgsystem.events.PrivateMessageEvent;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import com.google.inject.Inject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class MSGListener implements Listener, ChannelManager {

    @Inject
    private PlayerIgnoreManager playerIgnoreManager;

    @Inject
    private PMManager pmManager;

    @Inject
    private ChatSpyManager chatSpyManager;

    private final MSGConfig msg;

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public MSGListener() {
        YamlManager configManager = MSGSystem.getApi().getConfigManager();
        this.msg = configManager.getConfigAs(MSGConfig.class);
    }

    @EventHandler
    public void onPlayerChat(final ChatEvent e) {
        if (!e.getMessage().startsWith("#") || !(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
        e.setCancelled(true);
        String line = e.getMessage().substring(1);
        String[] str = line.split(" ");
        String name = str[0];
        String msg = String.join(" ", Arrays.copyOfRange(str, 1, str.length));
        this.handleChannel(sender, name, msg);
    }

    @Override
    public void registerChannel(String channel, String format, Predicate<ProxiedPlayer> playerPredicate) {
        Channel chan = new Channel(playerPredicate, format);
        this.channelMap.putIfAbsent(channel, chan);
    }

    @EventHandler
    public void onPlayerJoin(final PostLoginEvent e) {
        CompletableFuture.allOf(playerIgnoreManager.loadUserTask(e.getPlayer().getUniqueId()), pmManager.loadUserTask(e.getPlayer().getUniqueId()))
                .whenComplete((v, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    ProxyServer.getInstance().getLogger().info("MSG Data loaded for " + e.getPlayer().getName());
                });
    }


    @EventHandler
    public void onPlayerQuit(final ServerDisconnectEvent e) {
        playerIgnoreManager.saveUserTask(e.getPlayer().getUniqueId()).whenComplete((v, ex) -> ProxyServer.getInstance().getLogger().info("Ignore Data saved for " + e.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerPM(final PrivateMessageEvent e) {
        chatSpyManager.getOnlineChatSpyers().forEach(p -> {
            if (p.equals(e.getTarget()) || p.equals(e.getPlayer())) return;
            new AdvMessageBuilder(msg.getPure("msg.spy.prefix")).add(e.getLine()).sendPlayer(p);
        });
    }

    @Override
    public void unregisterChannel(String channel) {
        this.channelMap.remove(channel);
    }

    @Override
    public void handleChannel(ProxiedPlayer sender, String channel, String message) {
        Channel chan = this.channelMap.get(channel);
        if (chan == null || !chan.predicate.test(sender)) {
            new MessageBuilder(msg.get("msg.unknown-channel").replace("<channel>", channel)).sendPlayer(sender);
            return;
        }
        String[] format = chan.format.split("%player%");
        MessageBuilder player = new MessageBuilder(sender.getDisplayName()).hoverText(pmManager.getInfo(sender));
        TextComponent text = new AdvMessageBuilder(format[0]).add(player).add(format[1]).add(message).build();
        Collection<ProxiedPlayer> online = ProxyServer.getInstance().getPlayers();
        online.stream().filter(chan.predicate).forEach(pp -> pp.sendMessage(text));
    }

    private static class Channel {
        private final Predicate<ProxiedPlayer> predicate;
        private final String format;

        private Channel(Predicate<ProxiedPlayer> predicate, String format) {
            this.predicate = predicate;
            this.format = format;
        }
    }
}
