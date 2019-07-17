package com.ericlam.mc.msgsystem.main;

import com.ericlam.mc.bungee.hnmc.commands.caxerx.CommandRegister;
import com.ericlam.mc.bungee.hnmc.config.ConfigManager;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.msgsystem.MSGConfig;
import com.ericlam.mc.msgsystem.ModuleImplement;
import com.ericlam.mc.msgsystem.api.*;
import com.ericlam.mc.msgsystem.commands.*;
import com.ericlam.mc.msgsystem.listener.MSGChatListener;
import com.ericlam.mc.msgsystem.listener.MSGListener;
import com.ericlam.mc.msgsystem.manager.AnnouncementManager;
import com.ericlam.mc.msgsystem.manager.PlayerIgnoredPlayerManager;
import com.ericlam.mc.msgsystem.runnables.AutoAnnounceRunnable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MSGSystem extends Plugin implements MessageSystemAPI {

    private static MessageSystemAPI api;
    private ModuleImplement moduleImplement = new ModuleImplement();
    private ConfigManager configManager;
    private PlayerIgnoreManager playerIgnoreManager;
    private ListSerializer listSerializer;
    private PMManager pmManager;
    private AnnounceManager announceManager;
    private ChatSpyManager chatSpyManager;
    private AutoAnnounceRunnable autoAnnounceRunnable;
    private IllegalChatManager illegalChatManager;

    public static MessageSystemAPI getApi() {
        return api;
    }

    public static Optional<MessageSystemAPI> getApiSafe() {
        return Optional.ofNullable(api);
    }

    @Override
    public void onLoad() {
        try {
            configManager = HyperNiteMC.getAPI().registerConfig(new MSGConfig(this));
            configManager.setMsgConfig("config.yml");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        api = this;
        Injector injector = Guice.createInjector(moduleImplement);
        listSerializer = injector.getInstance(ListSerializer.class);
        playerIgnoreManager = injector.getInstance(PlayerIgnoreManager.class);
        pmManager = injector.getInstance(PMManager.class);
        announceManager = injector.getInstance(AnnounceManager.class);
        chatSpyManager = injector.getInstance(ChatSpyManager.class);
        illegalChatManager = injector.getInstance(IllegalChatManager.class);
    }

    @Override
    public void onEnable() {
        this.getProxy().getPluginManager().registerListener(this, new MSGListener(this));
        this.getProxy().getPluginManager().registerListener(this, (MSGChatListener) illegalChatManager);
        CommandRegister register = HyperNiteMC.getAPI().getCommandRegister();
        register.registerCommand(this, new IgnorePMCommand());
        register.registerCommand(this, new MessageCommand());
        register.registerCommand(this, new ReloadCommand());
        register.registerCommand(this, new ReplyCommand());
        register.registerCommand(this, new TogglePMCommand());
        register.registerCommand(this, new StaffCommand());
        autoAnnounceRunnable = new AutoAnnounceRunnable(announceManager);
        this.getProxy().getScheduler().schedule(this, autoAnnounceRunnable, 0L, 1L, TimeUnit.SECONDS);
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

    @Override
    public ChatSpyManager getChatSpyManager() {
        return chatSpyManager;
    }

    @Override
    public AnnounceManager getAnnounceManager() {
        return announceManager;
    }

    @Override
    public IllegalChatManager getIllegalChatManager() {
        return illegalChatManager;
    }

    @Override
    public void pluginReload() {
        configManager.reloadAllConfigs();
        configManager.setMsgConfig("config.yml");
        ((AnnouncementManager) announceManager).reloadAnnouncer();
        autoAnnounceRunnable.reloadAnnouncer();
    }
}
