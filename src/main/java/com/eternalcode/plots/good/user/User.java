package com.eternalcode.plots.good.user;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private String name;

    public User(UUID uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public void updateName(String name) {
        this.name = name;
    }

}