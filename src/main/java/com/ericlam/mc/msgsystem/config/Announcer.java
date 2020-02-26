package com.ericlam.mc.msgsystem.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Announcer {

    private Map<String, List<String>> messages;
    private String servers;
    private long delay;


    @JsonIgnore
    private LocalDateTime timer = LocalDateTime.now();

    public Map<String, List<String>> getMessages() {
        return messages;
    }

    public List<String> getServerInfos() {
        return servers.equalsIgnoreCase("[ALL]") ? null : Arrays.stream(servers.split("\\|")).distinct().collect(Collectors.toList());
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
