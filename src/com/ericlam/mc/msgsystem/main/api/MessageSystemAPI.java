package com.ericlam.mc.msgsystem.main.api;

import com.ericlam.mc.bungee.hnmc.config.ConfigManager;

public interface MessageSystemAPI {

    PlayerIgnoreManager getPlayerIgnoreManager();

    ConfigManager getConfigManager();

    ListSerializer getListSerializer();

    PMManager getPMManager();
}
