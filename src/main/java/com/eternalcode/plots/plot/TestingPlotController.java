package com.eternalcode.plots.plot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class TestingPlotController implements Listener {

    private final PlotService plotService;

    public TestingPlotController(PlotService plotService) {
        this.plotService = plotService;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Plot plot = this.plotService.getPlotForLocation(event.getBlock().getLocation());
        if (plot != null) {
            event.setCancelled(true);
        }
    }

}