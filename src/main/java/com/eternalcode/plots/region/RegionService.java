package com.eternalcode.plots.region;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RegionService {

    private final Set<Region> regions = new HashSet<>();

    public UUID createRegion(int size, int x, int z) {
        UUID regionUUID = UUID.randomUUID();
        Region region = new Region(regionUUID, size, x, z);
        regions.add(region);
        return regionUUID;
    }

    public Region getRegion(UUID regionUUID) {
        return regions.stream().filter(region -> region.getRegionUUID().equals(regionUUID)).findFirst().orElse(null);
    }

    public void deleteRegion(UUID regionUUID) {
        Region region = getRegion(regionUUID);
        if (region != null) {
            regions.remove(region);
        }
    }

    public void resizeRegion(UUID regionUUID, int newSize) {
        Region region = getRegion(regionUUID);
        if (region != null) {
            region.setSize(newSize);
        }
    }

}