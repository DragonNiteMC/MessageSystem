package com.ericlam.mc.msgsystem.manager;

import com.ericlam.mc.bungee.dnmc.SQLDataSource;
import com.ericlam.mc.bungee.dnmc.builders.AdvMessageBuilder;
import com.ericlam.mc.bungee.dnmc.builders.MessageBuilder;
import com.ericlam.mc.bungee.dnmc.config.YamlManager;
import com.ericlam.mc.bungee.dnmc.main.DragonNiteMC;
import com.ericlam.mc.msgsystem.api.PMManager;
import com.ericlam.mc.msgsystem.api.PlayerIgnoreManager;
import com.ericlam.mc.msgsystem.config.MSGConfig;
import com.ericlam.mc.msgsystem.events.PrivateMessageEvent;
import com.ericlam.mc.msgsystem.main.MSGSystem;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PrivateMessageManager implements PMManager {

    private Map<UUID, ProxiedPlayer> lastMessage = new HashMap<>();
    private Map<UUID, Boolean> pmDisabled = new HashMap<>();

    private MSGConfig msg;
    private SQLDataSource sqlDataSource;

    public PrivateMessageManager() {
        YamlManager configManager = MSGSystem.getApi().getConfigManager();
        this.msg = configManager.getConfigAs(MSGConfig.class);
        this.sqlDataSource = DragonNiteMC.getAPI().getSQLDataSource();
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
            MessageBuilder.sendMessage(player, DragonNiteMC.getAPI().getMainConfig().getNoThisPlayer());
            return;
        } else if (player.equals(target)) {
            MessageBuilder.sendMessage(player, msg.get("msg.send-self"));
            return;
        }

        if (this.isDisabledPM(player)) {
            MessageBuilder.sendMessage(player, msg.get("msg.toggle.disabled"));
            return;
        } else if (this.isDisabledPM(target)) {
            MessageBuilder.sendMessage(player, msg.get("msg.toggle.disabled-other").replace("<receiver>", target.getDisplayName()));
            return;
        }

        boolean targetIsIgnoringPlayer = false;

        PlayerIgnoreManager ignoreManager = MSGSystem.getApi().getPlayerIgnoreManager();


        if (ignoreManager.isIgnoredPlayer(player, target.getUniqueId())) {
            MessageBuilder.sendMessage(player, msg.get("msg.ignore.ignoring").replace("<target>", target.getDisplayName()));
            return;
        } else if (ignoreManager.isIgnoredPlayer(target, player.getUniqueId())) {
            targetIsIgnoringPlayer = true;
        }


        TextComponent format = this.getPrivateMessage(player, target, message);
        PrivateMessageEvent event = new PrivateMessageEvent(player, target, format, message);
        ProxyServer.getInstance().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.changeLastMessager(player, target);
        final TextComponent finalMsg = new AdvMessageBuilder(msg.getPure("prefix")).add(format).build();
        player.sendMessage(finalMsg);
        if (!targetIsIgnoringPlayer) {
            this.changeLastMessager(target, player);
            target.sendMessage(finalMsg);
        }
    }

    @Override
    public TextComponent getPrivateMessage(ProxiedPlayer player, ProxiedPlayer target, String message) {
        String[] msgComponent = msg.getPure("msg-format").split("<sender>");
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
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());
        String group = user == null ? "§c[! 加載失敗]" : this.getGroupAlias().containsKey(user.getPrimaryGroup()) ? this.getGroupAlias().get(user.getPrimaryGroup()) : user.getPrimaryGroup();
        String server = Optional.ofNullable(this.getDisplayAlias().get(player.getServer().getInfo().getName())).orElse(player.getServer().getInfo().getName());
        return msg.playerInfo.stream()
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
        this.saveUserTask(player.getUniqueId()).whenComplete((v, ex) -> ProxyServer.getInstance().getLogger().info("Toggle data saved for " + player.getUniqueId()));
    }

    @Override
    public Map<String, String> getDisplayAlias() {
        return msg.serverAlias;
    }

    @Override
    public Map<String, String> getGroupAlias() {
        return msg.groupAlias;
    }

    @Override
    public CompletableFuture<Void> loadUserTask(UUID uuid) {
        if (this.pmDisabled.containsKey(uuid)) return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT `toggle` FROM `MSG_Toggle_data` WHERE `uuid`=?")) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getBoolean("toggle");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }).thenAccept(bool -> this.pmDisabled.put(uuid, bool));
    }

    @Override
    public CompletableFuture<Void> saveUserTask(UUID uuid) {
        if (!pmDisabled.containsKey(uuid)) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO `MSG_Toggle_data` VALUES (?,?) ON DUPLICATE KEY UPDATE `toggle`=?")) {
                statement.setString(1, uuid.toString());
                statement.setBoolean(2, this.pmDisabled.get(uuid));
                statement.setBoolean(3, this.pmDisabled.get(uuid));
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
