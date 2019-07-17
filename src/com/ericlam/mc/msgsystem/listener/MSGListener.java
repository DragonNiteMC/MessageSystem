package com.ericlam.mc.msgsystem.listener;

import com.ericlam.mc.bungee.hnmc.builders.AdvMessageBuilder;
import com.ericlam.mc.bungee.hnmc.config.ConfigManager;
import com.ericlam.mc.msgsystem.api.ChatSpyManager;
import com.ericlam.mc.msgsystem.api.MessageSystemAPI;
import com.ericlam.mc.msgsystem.api.PMManager;
import com.ericlam.mc.msgsystem.api.PlayerIgnoreManager;
import com.ericlam.mc.msgsystem.events.PrivateMessageEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.CompletableFuture;

public class MSGListener implements Listener {

    private PlayerIgnoreManager playerIgnoreManager;

    private PMManager pmManager;

    private ChatSpyManager chatSpyManager;

    private ConfigManager configManager;


    public MSGListener(MessageSystemAPI api) {
        this.playerIgnoreManager = api.getPlayerIgnoreManager();
        this.pmManager = api.getPMManager();
        this.chatSpyManager = api.getChatSpyManager();
        this.configManager = api.getConfigManager();
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
            new AdvMessageBuilder(configManager.getPureMessage("msg.spy.prefix")).add(e.getLine()).sendPlayer(p);
        });
    }
}
