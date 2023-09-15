package com.eternalcode.plots.notgood.command.recoded.implementation;

import com.eternalcode.plots.notgood.gui.PlotListInventory;
import com.eternalcode.plots.notgood.gui.PlotPanelInventory;
import dev.rollczi.litecommands.command.amount.Required;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

@Route(name = "plot panel")
@Permission("plots.command.panel")
public class PanelCommand {

    private final PlotListInventory plotListInventory;
    private final PlotPanelInventory plotPanelInventory;

    public PanelCommand(PlotListInventory plotListInventory, PlotPanelInventory plotPanelInventory) {
        this.plotListInventory = plotListInventory;
        this.plotPanelInventory = plotPanelInventory;
    }

    @Required(1)
    @Execute
    void panel(Player player, Plot plot) {
        this.plotPanelInventory.open(player, plot);
    }

    @Required(0)
    @Execute
    void panel(Player player) {
        this.plotListInventory.open(player);
    }

}
