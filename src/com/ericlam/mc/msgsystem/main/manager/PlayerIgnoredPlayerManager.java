package com.ericlam.mc.msgsystem.main.manager;

import com.ericlam.mc.bungee.hnmc.SQLDataSource;
import com.ericlam.mc.bungee.hnmc.container.OfflinePlayer;
import com.ericlam.mc.bungee.hnmc.function.ResultParser;
import com.ericlam.mc.bungee.hnmc.main.HyperNiteMC;
import com.ericlam.mc.msgsystem.main.api.ListSerializer;
import com.ericlam.mc.msgsystem.main.api.PlayerIgnoreManager;
import com.google.inject.Inject;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerIgnoredPlayerManager implements PlayerIgnoreManager {

    private Map<UUID, List<OfflinePlayer>> ignoredList = new HashMap<>();

    private Set<UUID> saved = new HashSet<>();

    private SQLDataSource sqlDataSource;

    @Inject
    private ListSerializer listSerializer;

    public PlayerIgnoredPlayerManager(){
        this.sqlDataSource = HyperNiteMC.getAPI().getSQLDataSource();
        CompletableFuture.runAsync(()->{
            try(Connection connection = sqlDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `MSG_Ignore_table` (`uuid` VARCHAR(40) PRIMARY KEY NOT NULL, `list` TEXT NOT NULL )")) {
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<OfflinePlayer> getIgnoredPlayers(UUID uuid){
        return ignoredList.getOrDefault(uuid, new ArrayList<>());
    }

    @Override
    public boolean isIgnoredPlayer(ProxiedPlayer player, UUID target){
        return ignoredList.getOrDefault(player.getUniqueId(), new ArrayList<>()).stream().anyMatch(off->off.getUniqueId().equals(target));
    }

    @Override
    public void addIgnorePlayer(ProxiedPlayer player, OfflinePlayer target){
        List<OfflinePlayer> list = ignoredList.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (list.contains(target)) return;
        ResultParser.check(() -> list.add(target)).ifTrue(() -> this.saved.remove(player.getUniqueId()));
    }

    @Override
    public void removeIgnoredPlayer(ProxiedPlayer player, OfflinePlayer target){
        List<OfflinePlayer> list = ignoredList.getOrDefault(player.getUniqueId(), new ArrayList<>());
        ResultParser.check(() -> list.remove(target)).ifTrue(() -> this.saved.remove(player.getUniqueId()));
    }

    private void saveUserSingle(Connection connection, UUID uuid) throws SQLException {
        final String serial = listSerializer.serialize(ignoredList.get(uuid));
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO `MSG_Ignore_table` VALUES (?,?) ON DUPLICATE KEY UPDATE `list`=?")){
            statement.setString(1,uuid.toString());
            statement.setString(2,serial);
            statement.setString(3, serial);
            statement.execute();
            this.saved.add(uuid);
        }
    }

    @Override
    public CompletableFuture<Void> saveUserTask(UUID uuid){
        if (!ignoredList.containsKey(uuid)) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(()->{
            if (saved.contains(uuid)) return;
            try(Connection connection = sqlDataSource.getConnection()){
                this.saveUserSingle(connection, uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    public void saveUsersTask(){
        ignoredList.forEach((k, v)->{
            try(Connection connection = sqlDataSource.getConnection()) {
                if (saved.contains(k)) return;
                this.saveUserSingle(connection, k);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> loadUserTask(UUID uuid){
        if (ignoredList.containsKey(uuid)) return CompletableFuture.completedFuture(null);
        return CompletableFuture.runAsync(()->{
            try(Connection connection = sqlDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT `list` FROM `MSG_Ignore_table` WHERE `uuid`=?")) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()){
                    final String serial = resultSet.getString("list");
                    List<OfflinePlayer> list = listSerializer.deserialize(serial, OfflinePlayer.class);
                    this.ignoredList.put(uuid, list);
                }else{
                    this.ignoredList.put(uuid, new ArrayList<>());
                }
                this.saved.add(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
