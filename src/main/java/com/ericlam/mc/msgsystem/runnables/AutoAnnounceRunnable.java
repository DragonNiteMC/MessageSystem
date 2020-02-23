package com.ericlam.mc.msgsystem.runnables;

import com.ericlam.mc.msgsystem.api.AnnounceManager;
import com.ericlam.mc.msgsystem.container.CircularIterator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutoAnnounceRunnable implements Runnable {

    private AnnounceManager announceManager;
    private Map<String, CircularIterator<String>> preSend = new HashMap<>();

    public AutoAnnounceRunnable(AnnounceManager announceManager) {
        this.announceManager = announceManager;
        this.reloadAnnouncer();
    }

    public void reloadAnnouncer() {
        this.preSend.clear();
        announceManager.getAnnouncerMap().forEach((k, v) -> {
            Set<String> sectionKeys = v.getMessages().keySet();
            this.preSend.put(k, new CircularIterator<>(sectionKeys));
        });
    }

    @Override
    public void run() {
        announceManager.getAnnouncerMap().forEach((key, announcer) -> {
            if (!announcer.shouldSend()) return;
            if (!this.preSend.containsKey(key)) return;
            CircularIterator<String> circularIterator = this.preSend.get(key);
            if (!circularIterator.hasNext()) return;
            String sectionKey = circularIterator.next();
            announceManager.sendAnnouncement(key, sectionKey);
            announcer.setTimer();
        });
    }
}
