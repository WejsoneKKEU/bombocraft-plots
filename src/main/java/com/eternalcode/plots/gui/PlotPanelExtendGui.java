package com.eternalcode.plots.gui;

import com.eternalcode.plots.configuration.implementations.PluginConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotExtendConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import com.eternalcode.plots.features.extend.PlotExtend;
import com.eternalcode.plots.hook.VaultProvider;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.utils.VariablesUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

class PlotPanelExtendGui implements GuiProvider {

    private final VaultProvider vaultProvider;
    private final GuiActions guiActions;
    private final Plot plot;
    private final PluginConfiguration pluginConfiguration;
    private final PlotManager plotManager;
    private final PlotExtendConfiguration plotExtendConfig;
    private final MiniMessage miniMessage;

    PlotPanelExtendGui(VaultProvider vaultProvider, GuiActions guiActions, Plot plot, PluginConfiguration pluginConfiguration, PlotManager plotManager, PlotExtendConfiguration plotExtendConfiguration, MiniMessage miniMessage) {
        this.vaultProvider = vaultProvider;
        this.guiActions = guiActions;
        this.plot = plot;
        this.pluginConfiguration = pluginConfiguration;
        this.plotManager = plotManager;
        this.plotExtendConfig = plotExtendConfiguration;
        this.miniMessage = miniMessage;
    }

    @Override
    public Gui getGui() {
        String title = this.plotExtendConfig.title;

        Gui gui = Gui.gui()
            .title(miniMessage.deserialize(VariablesUtils.parsePlotVars(this.plot, title)))
            .rows(this.plotExtendConfig.rows)
            .disableAllInteractions()
            .create();

        if (this.plotExtendConfig.filler.getFiller().isPresent()) {
            gui.getFiller().fill(this.plotExtendConfig.filler.getFiller().get());
        }
        if (this.plotExtendConfig.border.getBorder().isPresent()) {
            gui.getFiller().fillBorder(this.plotExtendConfig.border.getBorder().get());
        }

        PlotExtend plotExtend = PlotExtend.create(this.plot, this.vaultProvider, this.plotManager, this.pluginConfiguration);

        /* Items */

        for (ConfigItem configItem : this.plotExtendConfig.items.values()) {

            configItem = ConfigItem.replacePlotVariables(configItem, plot);

            ItemStack itemStack = configItem.getItemStack(this.miniMessage);
            GuiItem guiItem = new GuiItem(itemStack);

            this.guiActions.setupAction(configItem.getAction(), guiItem, this.plot);

            gui.setItem(configItem.getSlot(), guiItem);
        }

        /* Extend Item */

        ConfigItem configItem = ConfigItem.replacePlotVariables(this.plotExtendConfig.extendItem, this.plot);

        configItem.name = configItem.name.replace("{NEW_SIZE}", plotExtend.getNewSize(plot) + "");
        configItem.name = configItem.name.replace("{OLD_SIZE}", this.plot.getRegion().getSize() + "");
        configItem.name = configItem.name.replace("{PRIZE}", plotExtend.getCostsService().getCosts());
        configItem.lore = VariablesUtils.parseVariable(configItem.lore, "{NEW_SIZE}", plotExtend.getNewSize(plot) + "");
        configItem.lore = VariablesUtils.parseVariable(configItem.lore, "{OLD_SIZE}", this.plot.getRegion().getSize() + "");
        configItem.lore = VariablesUtils.parseVariable(configItem.lore, "{PRIZE}", plotExtend.getCostsService().getCosts());

        ItemStack itemStack = configItem.getItemStack(this.miniMessage);
        GuiItem guiItem = new GuiItem(itemStack);

        this.guiActions.setupAction(this.plotExtendConfig.extendItem.action, guiItem, this.plot);

        gui.setItem(this.plotExtendConfig.extendItem.getSlot(), guiItem);

        return gui;
    }
}
