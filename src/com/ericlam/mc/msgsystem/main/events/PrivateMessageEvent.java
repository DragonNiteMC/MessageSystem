package com.ericlam.mc.msgsystem.main.events;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;

public class PrivateMessageEvent extends PlayerEvent implements Cancellable {

    private ProxiedPlayer target;
    private TextComponent line;
    private String message;
    private boolean cancel;

    public PrivateMessageEvent(ProxiedPlayer player, ProxiedPlayer target, TextComponent line, String message) {
        super(player);
        this.target = target;
        this.line = line;
        this.message = message;
        this.cancel = false;
    }

    public ProxiedPlayer getTarget() {
        return target;
    }

    public TextComponent getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }
}
