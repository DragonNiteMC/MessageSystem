package com.ericlam.mc.msgsystem.api;

import com.ericlam.mc.bungee.dnmc.config.YamlManager;

public interface MessageSystemAPI {

    PlayerIgnoreManager getPlayerIgnoreManager();

    YamlManager getConfigManager();

    ListSerializer getListSerializer();

    ChannelManager getChannelManager();

    PMManager getPMManager();

    ChatSpyManager getChatSpyManager();

    AnnounceManager getAnnounceManager();

    IllegalChatManager getIllegalChatManager();

    void pluginReload();
}
