package com.eternalcode.plots.feature.border;

import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.util.old.LocationUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BorderTask implements Runnable {

    private final Set<UUID> showed = new HashSet<>();
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 255, 0), 0.8F);
    private final PlotManager plotManager;
    private final Server server;

    public BorderTask(PlotManager plotManager, Server server) {
        this.plotManager = plotManager;
        this.server = server;
    }

    @Override
    public void run() {
        for (Player player : this.server.getOnlinePlayers()) {

            if (player == null || !player.isOnline()) {
                continue;
            }

            if (!hasShowed(player)) {
                continue;
            }

            for (Plot plot : this.plotManager.getPlots()) {

                if (!player.getLocation().getWorld().getName().equalsIgnoreCase(plot.getRegion().getCenter().getWorld())) {
                    continue;
                }

                if (LocationUtils.getDistance(player, plot) > 100) {
                    continue;
                }

                Set<Location> locations = LocationUtils.getBorder(plot);

                for (Location loc : locations) {
                    if (player.getLocation().distance(new Location(loc.getWorld(), loc.getX(), player.getLocation().getY(), loc.getZ())) < 100) {
                        player.spawnParticle(Particle.REDSTONE, new Location(loc.getWorld(), loc.getX(), player.getLocation().getBlockY(), loc.getZ()), 5, dustOptions);
                    }
                }
            }

        }
    }

    public boolean hasShowed(Player player) {
        return this.showed.contains(player.getUniqueId());
    }

    public void hide(Player player) {
        this.showed.remove(player.getUniqueId());
    }

    public void show(Player player) {
        this.showed.add(player.getUniqueId());
    }

}
