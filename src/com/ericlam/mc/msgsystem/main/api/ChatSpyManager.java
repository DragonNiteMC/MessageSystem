package com.ericlam.mc.msgsystem.main.api;

import java.util.UUID;

public interface ChatSpyManager {

    void addChatSpyer(UUID uuid);

    void removeChatSpyer(UUID uuid);

}
