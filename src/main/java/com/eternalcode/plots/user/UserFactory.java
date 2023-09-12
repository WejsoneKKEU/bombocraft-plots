package com.eternalcode.plots.user;

import java.util.UUID;

public class UserFactory {

    public User create(UUID uuid, String name) {
        return new User(uuid, name);
    }

}
