package com.ericlam.mc.msgsystem;

import com.ericlam.mc.msgsystem.api.*;
import com.ericlam.mc.msgsystem.listener.MSGChatListener;
import com.ericlam.mc.msgsystem.listener.MSGListener;
import com.ericlam.mc.msgsystem.manager.*;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class ModuleImplement implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ListSerializer.class).to(ArrayListSerializer.class).in(Scopes.SINGLETON);
        binder.bind(PlayerIgnoreManager.class).to(PlayerIgnoredPlayerManager.class).in(Scopes.SINGLETON);
        binder.bind(PMManager.class).to(PrivateMessageManager.class).in(Scopes.SINGLETON);
        binder.bind(ChatSpyManager.class).to(ChatSpyPlayerManager.class).in(Scopes.SINGLETON);
        binder.bind(AnnounceManager.class).to(AnnouncementManager.class).in(Scopes.SINGLETON);
        binder.bind(IllegalChatManager.class).to(MSGChatListener.class).in(Scopes.SINGLETON);
        binder.bind(ChannelManager.class).to(MSGListener.class).in(Scopes.SINGLETON);
    }
}
