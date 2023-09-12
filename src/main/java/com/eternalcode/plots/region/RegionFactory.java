package com.eternalcode.plots.region;

import org.bukkit.Location;

import java.util.UUID;

public class RegionFactory {

    public Region create(UUID regionUUID, int size, int extendLevel, Location posMax, Location posMin, Location center) {
        return new Region(regionUUID, size, extendLevel, posMax, posMin, center);
    }

    public Region createNew(int size, Location posMax, Location posMin, Location center) {
        return new Region(UUID.randomUUID(), size, 0, posMax, posMin, center);
    }

}
