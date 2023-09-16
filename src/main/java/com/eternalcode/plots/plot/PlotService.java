package com.eternalcode.plots.plot;

import com.eternalcode.plots.region.Region;
import com.eternalcode.plots.region.RegionService;
import org.bukkit.Location;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlotService {

    private final RegionService regionService;
    private final Set<Plot> plots = new HashSet<>();

    public PlotService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void createPlot(String name, Instant createdAt, Instant expireAt, int regionSize, int x, int z) {
        UUID regionId = this.regionService.createRegion(regionSize, x, z);
        Plot plot = new Plot(regionId, name, createdAt, expireAt);
        plots.add(plot);
    }

    public Plot getPlotForLocation(Location location) {
        for (Plot plot : plots) {
            Region region = this.regionService.getRegion(plot.plotId());
            if (region != null && region.isInRegion(location)) {
                return plot;
            }
        }
        return null;
    }

}