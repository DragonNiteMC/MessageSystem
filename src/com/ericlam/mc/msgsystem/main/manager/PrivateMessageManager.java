package com.ericlam.mc.msgsystem.main.manager;

import com.ericlam.mc.bungee.hnmc.SQLDataSource;
import com.ericlam.mc.bungee.hnmc.builders.AdvMessageBuilder;
import com.ericlam.mc.bungee.hnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.hnmc.config.ConfigManager;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import com.ericlam.mc.msgsystem.main.api.PMManager;
import com.ericlam.mc.msgsystem.main.api.PlayerIgnoreManager;
import com.ericlam.mc.msgsystem.main.events.PrivateMessageEvent;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PrivateMessageManager implements PMManager {

    private Map<UUID, ProxiedPlayer> lastMessage = new HashMap<>();
    private Map<UUID, Boolean> pmDisabled = new HashMap<>();

    private ConfigManager configManager;
    private SQLDataSource sqlDataSource;

    public PrivateMessageManager() {
        this.configManager = MSGSystem.getApi().getConfigManager();
        this.sqlDataSource = HyperNiteMC.getAPI().getSQLDataSource();
        CompletableFuture.runAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `MSG_Toggle_data` (`uuid` VARCHAR(40) PRIMARY KEY NOT NULL, `toggle` BOOLEAN NOT NULL )")) {
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Optional<ProxiedPlayer> getLastMessager(UUID uuid) {
        return Optional.ofNullable(this.lastMessage.get(uuid));
    }

    @Override
    public void changeLastMessager(ProxiedPlayer player, ProxiedPlayer target) {
        if (player.equals(target)) return;
        this.lastMessage.put(player.getUniqueId(), target);
    }

    @Override
    public void sendPrivateMessage(ProxiedPlayer player, ProxiedPlayer target, String message) {

        if (target == null) {
            MessageBuilder.sendMessage(player, HyperNiteMC.getAPI().getMainConfig().getNoThisPlayer());
            return;
        } else if (player.equals(target)) {
            MessageBuilder.sendMessage(player, configManager.getMessage("msg.send-self"));
            return;
        }

        if (this.isDisabledPM(player)) {
            MessageBuilder.sendMessage(player, configManager.getMessage("msg.toggle.disabled"));
            return;
        } else if (this.isDisabledPM(target)) {
            MessageBuilder.sendMessage(player, configManager.getMessage("msg.toggle.disabled-other").replace("<receiver>", target.getDisplayName()));
            return;
        }

        boolean targetIsIgnoringPlayer = false;

        PlayerIgnoreManager ignoreManager = MSGSystem.getApi().getPlayerIgnoreManager();


        if (ignoreManager.isIgnoredPlayer(player, target.getUniqueId())) {
            MessageBuilder.sendMessage(player, configManager.getMessage("msg.ignore.ignoring").replace("<target>", target.getDisplayName()));
            return;
        } else if (ignoreManager.isIgnoredPlayer(target, player.getUniqueId())) {
            targetIsIgnoringPlayer = true;
        }


        TextComponent format = this.getPrivateMessage(player, target, message);
        PrivateMessageEvent event = new PrivateMessageEvent(player, target, format, message);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.changeLastMessager(player, target);
        player.sendMessage(format);
        if (!targetIsIgnoringPlayer) target.sendMessage(format);
    }

    @Override
    public TextComponent getPrivateMessage(ProxiedPlayer player, ProxiedPlayer target, String message) {
        String[] msgComponent = configManager.getPureMessage("msg-format").split("<sender>");
        String first = msgComponent[0];
        String[] others = msgComponent[1].split("<receiver>");
        String second = others[0];
        String third = others[1].replace("<messages>", message);
        final MessageBuilder senderComponent = new MessageBuilder(player.getDisplayName()).hoverText(this.getInfo(player)).suggest("/msg ".concat(player.getName()));
        final MessageBuilder receiverComponent = new MessageBuilder(target.getDisplayName()).hoverText(this.getInfo(target)).suggest("/msg ".concat(target.getName()));
        return new AdvMessageBuilder(first).add(senderComponent).add(second).add(receiverComponent).add(third).build();
    }

    @Override
    public String[] getInfo(ProxiedPlayer player) {
        User user = LuckPerms.getApi().getUser(player.getUniqueId());
        String group = user == null ? "§c[! 加載失敗]" : user.getPrimaryGroup();
        String server = Optional.ofNullable(this.getDisplayAlias().get(player.getServer().getInfo().getName())).orElse(player.getServer().getInfo().getName());
        return Arrays.stream(configManager.getMessageList("player-info", false))
                .map(line -> line
                        .replace("<sender>", player.getDisplayName())
                        .replace("<server>", server)
                        .replace("<group>", group))
                .toArray(String[]::new);
    }

    @Override
    public boolean isDisabledPM(ProxiedPlayer player) {
        return pmDisabled.getOrDefault(player.getUniqueId(), false);
    }

    @Override
    public void setDisabledPM(ProxiedPlayer player, boolean disable) {
        pmDisabled.put(player.getUniqueId(), disable);
        this.saveUserTask(player.getUniqueId()).whenComplete((v,ex)-> ProxyServer.getInstance().getLogger().info("Toggle data saved for "+player.getUniqueId()));
    }

    @Override
    public Map<String, String> getDisplayAlias() {
        return configManager.getDataMap("sa", String.class, String.class);
    }

    @Override
    public CompletableFuture<Void> loadUserTask(UUID uuid) {
        if (this.pmDisabled.containsKey(uuid)) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT `toggle` FROM `MSG_Toggle_data` WHERE `uuid`=?")) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    boolean toggle = resultSet.getBoolean("toggle");
                    this.pmDisabled.put(uuid, toggle);
                } else {
                    this.pmDisabled.put(uuid, false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveUserTask(UUID uuid) {
        if (!pmDisabled.containsKey(uuid)) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(()->{
            try(Connection connection = sqlDataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `MSG_Toggle_data` VALUES (?,?) ON DUPLICATE KEY UPDATE `toggle`=?")){
                statement.setString(1, uuid.toString());
                statement.setBoolean(2, this.pmDisabled.get(uuid));
                statement.setBoolean(2, this.pmDisabled.get(uuid));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
