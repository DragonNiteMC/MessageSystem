package com.ericlam.mc.msgsystem.container;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Announcer {
    private Map<String, List<String>> messages;

    private List<String> servers;

    private long delay;

    private LocalDateTime timer;

    public Announcer(Map<String, List<String>> messages, List<String> serverInfos, long delay) {
        this.messages = messages;
        this.servers = serverInfos;
        this.delay = delay;
        this.timer = LocalDateTime.now();
    }

    public Map<String, List<String>> getMessages() {
        return messages;
    }

    public List<String> getServerInfos() {
        return servers;
    }

    public void setTimer() {
        timer = LocalDateTime.now();
    }


    public long getDelay() {
        return delay;
    }

    public boolean shouldSend() {
        Duration duration = Duration.between(timer, LocalDateTime.now());
        return duration.getSeconds() >= delay;
    }
}
