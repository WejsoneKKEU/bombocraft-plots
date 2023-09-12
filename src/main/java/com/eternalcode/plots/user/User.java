package com.eternalcode.plots.user;

import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private String name;

    User(UUID uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.uuid.equals(user.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    void setName(String name) {
        this.name = name;
    }
}
