package com.eternalcode.plots.listener.protection;

import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.plot.protection.ProtectionManager;
import com.eternalcode.plots.region.Region;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import panda.std.Option;

import java.util.UUID;

public class BlockFromToListener implements Listener {

    private final ProtectionManager protectionManager;
    private final ProtectionConfiguration config;
    private final PlotManager plotManager;
    private final Server server;

    public BlockFromToListener(ProtectionManager protectionManager, ProtectionConfiguration config, PlotManager plotManager, Server server) {
        this.protectionManager = protectionManager;
        this.config = config;
        this.plotManager = plotManager;
        this.server = server;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!config.getGriefing().getWaterLavaFlow().isProtection()) {
            return;
        }

        Block blockTo = event.getToBlock();
        Block block = event.getBlock();

        UUID uuid = this.protectionManager.getPlaced().getIfPresent(block);

        Player player = null;


        if (uuid != null) {
            player = this.server.getPlayer(uuid);
        }

        if (player != null) {

            if (protectionManager.hasBypass(player)) return;

            this.protectionManager.getPlaced().put(blockTo, player.getUniqueId());

            if (this.protectionManager.isAllowed(player, blockTo.getLocation())) {
                return;
            }
        }

        Option<Region> regionFromOpt = this.plotManager.getRegion(block.getLocation());
        Option<Region> regionToOpt = this.plotManager.getRegion(blockTo.getLocation());

        if (regionToOpt.isEmpty()) {
            return;
        }

        Region region = regionToOpt.get();

        Plot plot = this.plotManager.getPlot(region);

        if (regionFromOpt.isPresent()) {
            Region regionFrom = regionFromOpt.get();
            Plot plotFrom = this.plotManager.getPlot(regionFrom);
            if (plotFrom.equals(plot)) {
                return;
            }
        }

        event.setCancelled(true);
    }
}
