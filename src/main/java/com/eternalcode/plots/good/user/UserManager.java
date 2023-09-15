package com.eternalcode.plots.good.user;

import panda.std.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final Map<UUID, User> usersByUUID = new HashMap<>();
    private final UserRepository userRepository;

    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOrCreate(UUID uuid, String name) {
        return create(uuid, name).orElseGet(() -> this.usersByUUID.get(uuid));
    }

    public Option<User> create(UUID uuid, String name) {
        if (this.usersByUUID.containsKey(uuid)) {
            return Option.none();
        }

        User user = new User(uuid, name);
        this.usersByUUID.put(uuid, user);

        this.userRepository.saveUser(user);

        return Option.of(user);
    }

    public void updateName(User user, String name) {
        user.updateName(name);

        this.userRepository.saveUser(user);
    }

    public Collection<User> getUsersByUUID() {
        return Collections.unmodifiableCollection(this.usersByUUID.values());
    }

}
