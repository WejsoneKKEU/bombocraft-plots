package com.eternalcode.plots.region;

import org.bukkit.Location;
import org.bukkit.World;

public class RegionManager {

    private final RegionFactory regionFactory;

    public RegionManager(RegionFactory regionFactory) {
        this.regionFactory = regionFactory;
    }

    public Region create(int size, Location posMax, Location posMin, Location center) {
        return this.regionFactory.createNew(size, posMax, posMin, center);
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
