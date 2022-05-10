package com.ericlam.mc.msgsystem.main;

import com.ericlam.mc.bungee.dnmc.commands.caxerx.CommandRegister;
import com.ericlam.mc.bungee.dnmc.config.YamlManager;
import com.ericlam.mc.bungee.dnmc.main.DragoniteMC;
import com.ericlam.mc.msgsystem.ModuleImplement;
import com.ericlam.mc.msgsystem.api.*;
import com.ericlam.mc.msgsystem.commands.*;
import com.ericlam.mc.msgsystem.config.AnnounceConfig;
import com.ericlam.mc.msgsystem.config.ChatConfig;
import com.ericlam.mc.msgsystem.config.MSGConfig;
import com.ericlam.mc.msgsystem.listener.MSGChatListener;
import com.ericlam.mc.msgsystem.listener.MSGListener;
import com.ericlam.mc.msgsystem.manager.AnnouncementManager;
import com.ericlam.mc.msgsystem.manager.PlayerIgnoredPlayerManager;
import com.ericlam.mc.msgsystem.runnables.AutoAnnounceRunnable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MSGSystem extends Plugin implements MessageSystemAPI {

    private static MessageSystemAPI api;
    private final ModuleImplement moduleImplement = new ModuleImplement();
    private YamlManager configManager;
    private PlayerIgnoreManager playerIgnoreManager;
    private ListSerializer listSerializer;
    private PMManager pmManager;
    private AnnounceManager announceManager;
    private ChatSpyManager chatSpyManager;
    private AutoAnnounceRunnable autoAnnounceRunnable;
    private IllegalChatManager illegalChatManager;
    private ChannelManager channelManager;

    public static MessageSystemAPI getApi() {
        return api;
    }

    public static Optional<MessageSystemAPI> getApiSafe() {
        return Optional.ofNullable(api);
    }

    @Override
    public void onLoad() {
        configManager = DragoniteMC.getAPI().getConfigFactory(this)
                .register("config.yml", MSGConfig.class)
                .register("chat.yml", ChatConfig.class)
                .register("announce.yml", AnnounceConfig.class).dump();
        api = this;
        Injector injector = Guice.createInjector(moduleImplement);
        listSerializer = injector.getInstance(ListSerializer.class);
        playerIgnoreManager = injector.getInstance(PlayerIgnoreManager.class);
        pmManager = injector.getInstance(PMManager.class);
        announceManager = injector.getInstance(AnnounceManager.class);
        chatSpyManager = injector.getInstance(ChatSpyManager.class);
        illegalChatManager = injector.getInstance(IllegalChatManager.class);
        channelManager = injector.getInstance(ChannelManager.class);
    }

    @Override
    public void onEnable() {
        this.getProxy().getPluginManager().registerListener(this, (MSGListener) channelManager);
        this.getProxy().getPluginManager().registerListener(this, (MSGChatListener) illegalChatManager);
        CommandRegister register = DragoniteMC.getAPI().getCommandRegister();
        register.registerCommand(this, new IgnorePMCommand());
        register.registerCommand(this, new MessageCommand());
        register.registerCommand(this, new ReloadCommand());
        register.registerCommand(this, new ReplyCommand());
        register.registerCommand(this, new TogglePMCommand());
        register.registerCommand(this, new StaffCommand());
        autoAnnounceRunnable = new AutoAnnounceRunnable(announceManager);
        this.getProxy().getScheduler().schedule(this, autoAnnounceRunnable, 0L, 1L, TimeUnit.SECONDS);
        MSGConfig msg = configManager.getConfigAs(MSGConfig.class);
        msg.channels.forEach((channel, format) -> channelManager.registerChannel(channel, format, p -> p.hasPermission("hypernite.channel.".concat(channel))));
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
    public YamlManager getConfigManager() {
        return configManager;
    }

    @Override
    public ListSerializer getListSerializer() {
        return listSerializer;
    }

    @Override
    public ChannelManager getChannelManager() {
        return channelManager;
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
        configManager.reloadConfigs();
        ((AnnouncementManager) announceManager).reloadAnnouncer();
        autoAnnounceRunnable.reloadAnnouncer();
    }
}
