package com.ericlam.mc.msgsystem.container;

import com.ericlam.mc.bungee.hnmc.config.Component;
import com.ericlam.mc.bungee.hnmc.config.Prop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Announcer {

    @Prop
    private Map<String, List<String>> messages;

    @Prop
    private String servers;

    @Prop
    private long delay;

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
