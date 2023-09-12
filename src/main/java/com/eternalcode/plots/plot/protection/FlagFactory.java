package com.eternalcode.plots.plot.protection;

import java.util.UUID;

public class FlagFactory {

    public Flag create(UUID flagUUID, FlagType flagType, boolean flagStatus) {
        return new Flag(flagUUID, flagType, flagStatus);
    }
}
