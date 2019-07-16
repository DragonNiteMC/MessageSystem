package com.ericlam.mc.msgsystem.main;

import com.ericlam.mc.bungee.hnmc.config.ConfigManager;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.msgsystem.main.api.ListSerializer;
import com.ericlam.mc.msgsystem.main.api.MessageSystemAPI;
import com.ericlam.mc.msgsystem.main.api.PMManager;
import com.ericlam.mc.msgsystem.main.api.PlayerIgnoreManager;
import com.ericlam.mc.msgsystem.main.manager.PlayerIgnoredPlayerManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.Optional;

public class MSGSystem extends Plugin implements MessageSystemAPI {

    private static MessageSystemAPI api;
    private ModuleImplement moduleImplement = new ModuleImplement();
    private ConfigManager configManager;
    private PlayerIgnoreManager playerIgnoreManager;
    private ListSerializer listSerializer;
    private PMManager pmManager;

    public static MessageSystemAPI getApi() {
        return api;
    }

    public static Optional<MessageSystemAPI> getApiSafe() {
        return Optional.ofNullable(api);
    }

    @Override
    public void onLoad() {
        api = this;
        Injector injector = Guice.createInjector(moduleImplement);
        listSerializer = injector.getInstance(ListSerializer.class);
        playerIgnoreManager = injector.getInstance(PlayerIgnoreManager.class);
        pmManager = injector.getInstance(PMManager.class);
    }

    @Override
    public void onEnable() {
        try {
            configManager = HyperNiteMC.getAPI().registerConfig(new MSGConfig(this));
            configManager.setMsgConfig("format.yml");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.getProxy().getPluginManager().registerListener(this, new MSGListener(playerIgnoreManager));
    }

    @Override
    public void onDisable() {
        ((PlayerIgnoredPlayerManager) playerIgnoreManager).saveUsersTask();
    }

    @Override
    public PlayerIgnoreManager getPlayerIgnoreManager() {
        return playerIgnoreManager;
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public ListSerializer getListSerializer() {
        return listSerializer;
    }

    @Override
    public PMManager getPMManager() {
        return pmManager;
    }
}
