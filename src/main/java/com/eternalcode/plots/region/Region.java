package com.eternalcode.plots.region;

import org.bukkit.Location;

import java.util.UUID;

public class Region {

    private UUID regionUUID;
    private int size;
    private int x;
    private int z;

    public Region(UUID regionUUID, int size, int x, int z) {
        this.regionUUID = regionUUID;
        this.size = size;
        this.x = x;
        this.z = z;
    }

    public UUID getRegionUUID() {
        return regionUUID;
    }

    public void setRegionUUID(UUID regionUUID) {
        this.regionUUID = regionUUID;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public boolean isInRegion(Location location) {
        int coordinateX = Math.abs(location.getBlockX() - this.x);
        int coordinateZ = Math.abs(location.getBlockZ() - this.z);

        return coordinateX <= this.size && coordinateZ <= this.size;
    }
}
