package com.eternalcode.plots.feature.delete;

import com.eternalcode.plots.adventure.LegacyUtils;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.position.PositionAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlotDelete {

    private final PlotManager plotManager;

    public PlotDelete(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    public void deletePlot(Plot plot, Player deleter) {
        this.plotManager.delete(plot);
        PositionAdapter.convert(plot.getRegion().getCenter()).getBlock().setType(Material.AIR);
        Bukkit.broadcastMessage(LegacyUtils.color("<#14E36C> Gracz " + deleter.getName() + " usunął działkę " + plot.getName()));
    }
}
