package com.ericlam.mc.msgsystem.api;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.function.Predicate;

public interface ChannelManager {

    /**
     * 註冊頻道。
     * <p>
     * Format 格式:
     * <p>
     * %player% 玩家名稱佔位符
     *
     * @param channel         頻道名稱
     * @param format          文字格式
     * @param playerPredicate 偵測玩家是否能使用頻道
     */
    void registerChannel(String channel, String format, Predicate<ProxiedPlayer> playerPredicate);

    /**
     * 取消註冊頻道
     *
     * @param channel 頻道名稱
     */
    void unregisterChannel(String channel);

    /**
     * 處理頻道
     *
     * @param sender  發送者
     * @param channel 頻道名稱
     * @param message 訊息
     */
    void handleChannel(ProxiedPlayer sender, String channel, String message);
}
