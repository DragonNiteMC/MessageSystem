package com.ericlam.mc.msgsystem.main;

import com.ericlam.mc.msgsystem.main.api.MessageSystemAPI;
import com.ericlam.mc.msgsystem.main.api.PMManager;
import com.ericlam.mc.msgsystem.main.api.PlayerIgnoreManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.CompletableFuture;

public class MSGListener implements Listener {

    private PlayerIgnoreManager playerIgnoreManager;

    private PMManager pmManager;


    MSGListener(MessageSystemAPI api) {
        this.playerIgnoreManager = api.getPlayerIgnoreManager();
        this.pmManager = api.getPMManager();
    }

    @EventHandler
    public void onPlayerJoin(final PostLoginEvent e){
        CompletableFuture.allOf(playerIgnoreManager.loadUserTask(e.getPlayer().getUniqueId()), pmManager.loadUserTask(e.getPlayer().getUniqueId()))
                .whenComplete((v,ex)-> ProxyServer.getInstance().getLogger().info("MSG Data loaded for "+e.getPlayer().getName()));
    }


    @EventHandler
    public void onPlayerQuit(final ServerDisconnectEvent e){
        playerIgnoreManager.saveUserTask(e.getPlayer().getUniqueId()).whenComplete((v,ex)-> ProxyServer.getInstance().getLogger().info("Ignore Data saved for "+e.getPlayer().getName()));
    }
}
