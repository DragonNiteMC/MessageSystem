package com.ericlam.mc.msgsystem.manager;

import com.ericlam.mc.bungee.hnmc.SQLDataSource;
import com.ericlam.mc.bungee.hnmc.container.OfflinePlayer;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.msgsystem.api.ListSerializer;
import com.ericlam.mc.msgsystem.api.PlayerIgnoreManager;
import com.google.inject.Inject;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerIgnoredPlayerManager implements PlayerIgnoreManager {

    private final Map<UUID, List<OfflinePlayer>> ignoredList = new HashMap<>();

    private final SQLDataSource sqlDataSource;

    @Inject
    private ListSerializer listSerializer;

    public PlayerIgnoredPlayerManager() {
        this.sqlDataSource = HyperNiteMC.getAPI().getSQLDataSource();
        CompletableFuture.runAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `MSG_Ignore_table` (`uuid` VARCHAR(40) PRIMARY KEY NOT NULL, `list` TEXT NOT NULL )")) {
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<OfflinePlayer> getIgnoredPlayers(UUID uuid) {
        return ignoredList.getOrDefault(uuid, new ArrayList<>());
    }

    @Override
    public boolean isIgnoredPlayer(ProxiedPlayer player, UUID target) {
        return ignoredList.getOrDefault(player.getUniqueId(), new ArrayList<>()).stream().anyMatch(off -> off.getUniqueId().equals(target));
    }

    @Override
    public void addIgnorePlayer(ProxiedPlayer player, OfflinePlayer target) {
        List<OfflinePlayer> list = ignoredList.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (list.contains(target)) return;
        list.add(target);
        this.ignoredList.put(player.getUniqueId(), list);
    }

    @Override
    public void removeIgnoredPlayer(ProxiedPlayer player, OfflinePlayer target) {
        List<OfflinePlayer> list = ignoredList.getOrDefault(player.getUniqueId(), new ArrayList<>());
        list.remove(target);
        this.ignoredList.put(player.getUniqueId(), list);
    }

    private void saveUserSingle(Connection connection, UUID uuid) throws SQLException {
        final String serial = listSerializer.serialize(ignoredList.get(uuid));
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `MSG_Ignore_table` VALUES (?,?) ON DUPLICATE KEY UPDATE `list`=?")) {
            statement.setString(1, uuid.toString());
            statement.setString(2, serial);
            statement.setString(3, serial);
            statement.execute();
            ignoredList.remove(uuid);
        }
    }

    @Override
    public CompletableFuture<Void> saveUserTask(UUID uuid) {
        if (!ignoredList.containsKey(uuid)) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection()) {
                this.saveUserSingle(connection, uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    public void saveUsersTask() {
        try (Connection connection = sqlDataSource.getConnection()) {
            for (UUID key : ignoredList.keySet()) {
                saveUserSingle(connection, key);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> loadUserTask(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = sqlDataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT `list` FROM `MSG_Ignore_table` WHERE `uuid`=?")) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final String serial = resultSet.getString("list");
                    return listSerializer.deserialize(serial, OfflinePlayer.class);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ArrayList<OfflinePlayer>();
        }).thenAccept(offlinePlayers -> this.ignoredList.put(uuid, offlinePlayers));
    }
}
