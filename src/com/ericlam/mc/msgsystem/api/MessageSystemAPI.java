package com.ericlam.mc.msgsystem.api;

import com.ericlam.mc.bungee.hnmc.config.ConfigManager;

public interface MessageSystemAPI {

    PlayerIgnoreManager getPlayerIgnoreManager();

    ConfigManager getConfigManager();

    ListSerializer getListSerializer();

    PMManager getPMManager();

    ChatSpyManager getChatSpyManager();

    AnnounceManager getAnnounceManager();

    IllegalChatManager getIllegalChatManager();

    void pluginReload();
}
