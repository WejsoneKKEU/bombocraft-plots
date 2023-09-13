package com.eternalcode.plots.plot.member;

import com.eternalcode.plots.user.User;

import java.util.Objects;
import java.util.UUID;

public class PlotMember {

    private final UUID uuid;
    private final User user;

    public PlotMember(UUID uuid, User user) {
        this.uuid = uuid;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlotMember plotMember = (PlotMember) o;
        return this.uuid.equals(plotMember.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public User getUser() {
        return this.user;
    }
}
