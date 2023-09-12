package com.eternalcode.plots.plot.protection;

import java.util.UUID;

public class Flag {

    private final UUID uuid;
    private final FlagType flagType;
    private boolean status;

    Flag(UUID uuid, FlagType flagType, boolean status) {
        this.uuid = uuid;
        this.flagType = flagType;
        this.status = status;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public FlagType getFlagType() {
        return this.flagType;
    }

    public boolean getStatus() {
        return this.status;
    }

    void setStatus(boolean status) {
        this.status = status;
    }
}
