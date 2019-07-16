package com.ericlam.mc.msgsystem.main;

import com.ericlam.mc.msgsystem.main.api.ListSerializer;
import com.ericlam.mc.msgsystem.main.api.PMManager;
import com.ericlam.mc.msgsystem.main.api.PlayerIgnoreManager;
import com.ericlam.mc.msgsystem.main.manager.ArrayListSerializer;
import com.ericlam.mc.msgsystem.main.manager.PlayerIgnoredPlayerManager;
import com.ericlam.mc.msgsystem.main.manager.PrivateMessageManager;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class ModuleImplement implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ListSerializer.class).to(ArrayListSerializer.class).in(Scopes.SINGLETON);
        binder.bind(PlayerIgnoreManager.class).to(PlayerIgnoredPlayerManager.class).in(Scopes.SINGLETON);
        binder.bind(PMManager.class).to(PrivateMessageManager.class).in(Scopes.SINGLETON);
    }
}
