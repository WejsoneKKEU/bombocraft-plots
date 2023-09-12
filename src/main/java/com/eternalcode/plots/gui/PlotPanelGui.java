package com.eternalcode.plots.gui;

import com.eternalcode.plots.configuration.implementations.gui.PlotPanelConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.utils.VariablesUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.minimessage.MiniMessage;

class PlotPanelGui implements GuiProvider {

    private final GuiActions guiActions;
    private final PlotPanelConfiguration plotPanelConfig;
    private final MiniMessage miniMessage;
    private final Plot plot;

    PlotPanelGui(GuiActions guiActions, PlotPanelConfiguration plotPanelConfig, MiniMessage miniMessage, Plot plot) {
        this.guiActions = guiActions;
        this.plotPanelConfig = plotPanelConfig;
        this.miniMessage = miniMessage;
        this.plot = plot;
    }

    @Override
    public Gui getGui() {
        String title = this.plotPanelConfig.title;

        Gui gui = Gui.gui()
            .title(this.miniMessage.deserialize(VariablesUtils.parsePlotVars(this.plot, title)))
            .rows(this.plotPanelConfig.rows)
            .disableAllInteractions()
            .create();

        if (this.plotPanelConfig.filler.getFiller().isPresent()) {
            gui.getFiller().fill(this.plotPanelConfig.filler.getFiller().get());
        }

        if (this.plotPanelConfig.border.getBorder().isPresent()) {
            gui.getFiller().fillBorder(this.plotPanelConfig.border.getBorder().get());
        }

        /* Items */

        for (ConfigItem configItem : this.plotPanelConfig.items.values()) {

            configItem = ConfigItem.replacePlotVariables(configItem, plot);
            GuiItem guiItem = new GuiItem(configItem.getItemStack(this.miniMessage));

            this.guiActions.setupAction(configItem.getAction(), guiItem, plot);

            gui.setItem(configItem.getSlot(), guiItem);
        }

        return gui;
    }
}
