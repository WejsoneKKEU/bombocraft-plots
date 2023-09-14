package com.eternalcode.plots.plot.old.region;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class RegionManager {

    public Region create(int size, Location posMax, Location posMin, Location center) {
        return new Region(UUID.randomUUID(), size, 0, posMax, posMin, center);
    }

    public void setSize(Region region, int size) {
        region.setSize(size);
    }

    public void setExtendLevel(Region region, int level) {
        region.setExtendLevel(level);
    }

    public boolean isInRegion(Region region, Location location) {
        double x = location.getX();
        double z = location.getZ();
        World world = location.getWorld();

        if (world == null) {
            return false;
        }

        return x <= region.getPosMax().getX() &&
            x >= region.getPosMin().getX() &&
            z <= region.getPosMax().getZ() &&
            z >= region.getPosMin().getZ() &&
            world.getName().equalsIgnoreCase(location.getWorld().getName());
    }


}