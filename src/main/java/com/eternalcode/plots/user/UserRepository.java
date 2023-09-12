package com.eternalcode.plots.user;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {

    CompletableFuture<List<User>> getUsers();

    CompletableFuture<User> getUser(UUID uuid);

    void saveUser(User user);

}
