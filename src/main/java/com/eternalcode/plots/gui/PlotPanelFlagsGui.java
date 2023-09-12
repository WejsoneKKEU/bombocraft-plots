package com.eternalcode.plots.gui;

import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotFlagsConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.protection.FlagType;
import com.eternalcode.plots.plot.protection.ProtectionManager;
import com.eternalcode.plots.utils.VariablesUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

class PlotPanelFlagsGui implements GuiProvider {

    private final GuiActions guiActions;
    private final ProtectionConfiguration protectionConfig;
    private final PlotFlagsConfiguration plotFlagsConfig;
    private final ProtectionManager protectionManager;
    private final MiniMessage miniMessage;
    private final GuiFactory guiFactory;
    private final Plot plot;

    PlotPanelFlagsGui(GuiActions guiActions, ProtectionConfiguration protectionConfig, PlotFlagsConfiguration plotFlagsConfig, ProtectionManager protectionManager, MiniMessage miniMessage, GuiFactory guiFactory, Plot plot) {
        this.guiActions = guiActions;
        this.protectionConfig = protectionConfig;
        this.plotFlagsConfig = plotFlagsConfig;
        this.protectionManager = protectionManager;
        this.miniMessage = miniMessage;
        this.guiFactory = guiFactory;
        this.plot = plot;
    }

    @Override
    public Gui getGui() {
        String title = this.plotFlagsConfig.title;

        Gui gui = Gui.gui()
            .title(this.miniMessage.deserialize(VariablesUtils.parsePlotVars(plot, title)))
            .rows(this.plotFlagsConfig.rows)
            .disableAllInteractions()
            .create();

        if (this.plotFlagsConfig.filler.getFiller().isPresent()) {
            gui.getFiller().fill(this.plotFlagsConfig.filler.getFiller().get());
        }
        if (this.plotFlagsConfig.border.getBorder().isPresent()) {
            gui.getFiller().fillBorder(this.plotFlagsConfig.border.getBorder().get());
        }

        /* Items */

        for (ConfigItem configItem : this.plotFlagsConfig.items.values()) {

            configItem = ConfigItem.replacePlotVariables(configItem, this.plot);
            GuiItem guiItem = new GuiItem(configItem.getItemStack(this.miniMessage));

            this.guiActions.setupAction(configItem.getAction(), guiItem, this.plot);

            gui.setItem(configItem.getSlot(), guiItem);
        }

        /* Flags */

        for (ProtectionConfiguration.ConfigProtection flag : this.getConfigProtections(this.protectionConfig)) {

            if (!flag.isEditInGui()) {
                continue;
            }

            boolean status = isProtection(this.plot, flag.getType());

            PlotFlagsConfiguration.FlagItem flagItem = this.plotFlagsConfig.flagItem.replaceFlagVariables(
                flag.getGuiMaterial(),
                flag.getGuiName(),
                flag.getGuiDescription(),
                status,
                this.plotFlagsConfig.enabledStatus,
                this.plotFlagsConfig.disabledStatus
            );

            ItemStack itemStack = flagItem.getItemStack(this.miniMessage);
            GuiItem guiItem = new GuiItem(itemStack);

            guiItem.setAction(event -> {

                setStatus(this.plot, flag.getType(), !isProtection(this.plot, flag.getType()));
                this.guiFactory.createPlotFlagsGui(this.plot).getGui().open(event.getWhoClicked());

            });

            gui.addItem(guiItem);
        }


        return gui;
    }

    private Collection<ProtectionConfiguration.ConfigProtection> getConfigProtections(ProtectionConfiguration flagsConfig) {
        return flagsConfig.getProtections().values();
    }

    private void setStatus(Plot plot, FlagType flagType, boolean status) {
        this.protectionManager.setProtection(plot, plot.getProtection(), flagType, status);
    }

    private boolean isProtection(Plot plot, FlagType flagType) {
        return this.protectionManager.isProtection(plot, flagType);
    }
}
