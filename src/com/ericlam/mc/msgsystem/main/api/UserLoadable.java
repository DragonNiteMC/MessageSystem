package com.ericlam.mc.msgsystem.main.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserLoadable {

    CompletableFuture<Void> loadUserTask(UUID uuid);

    CompletableFuture<Void> saveUserTask(UUID uuid);

}
