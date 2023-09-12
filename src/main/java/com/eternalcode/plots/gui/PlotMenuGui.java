package com.eternalcode.plots.gui;

import com.eternalcode.plots.configuration.implementations.gui.PlotMenuConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigAction;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.user.User;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.util.SkullUtil;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class PlotMenuGui implements GuiProvider {

    private final GuiActions guiActions;
    private final PlotMenuConfiguration plotSelectorConfig;
    private final PlotManager plotManager;
    private final MiniMessage miniMessage;
    private final User user;

    PlotMenuGui(GuiActions guiActions, PlotMenuConfiguration plotSelectorConfig, PlotManager plotManager, MiniMessage miniMessage, User user) {
        this.guiActions = guiActions;
        this.plotSelectorConfig = plotSelectorConfig;
        this.plotManager = plotManager;
        this.miniMessage = miniMessage;
        this.user = user;
    }

    @Override
    public Gui getGui() {
        return getGui(0);
    }

    public Gui getGui(int page) {
        if (page < 0) page = 0;

        int plotsInGui = this.plotSelectorConfig.plotsPlaces.size();

        Gui gui = Gui.gui()
            .title(this.miniMessage.deserialize(this.plotSelectorConfig.title))
            .rows(this.plotSelectorConfig.rows)
            .disableAllInteractions()
            .create();

        if (this.plotSelectorConfig.filler.getFiller().isPresent()) {
            gui.getFiller().fill(this.plotSelectorConfig.filler.getFiller().get());
        }
        if (this.plotSelectorConfig.border.getBorder().isPresent()) {
            gui.getFiller().fillBorder(this.plotSelectorConfig.border.getBorder().get());
        }

        Set<Plot> userPlots = this.plotManager.getPlots(user);

        /* Items */

        int finalPage = page;

        for (ConfigItem configItem : this.plotSelectorConfig.items.values()) {
            GuiItem guiItem = new GuiItem(configItem.getItemStack(this.miniMessage));

            if (configItem.getAction() == ConfigAction.PREVIOUS_PAGE) {
                guiItem.setAction(event -> {
                    if (userPlots.isEmpty()) {
                        return;
                    }
                    if ((finalPage - 1) * plotsInGui < userPlots.size()) {
                        getGui(finalPage - 1).open(event.getWhoClicked());
                    }
                });
            }

            if (configItem.getAction() == ConfigAction.NEXT_PAGE) {
                guiItem.setAction(event -> {
                    if (userPlots.isEmpty()) {
                        return;
                    }
                    if ((finalPage + 1) * plotsInGui < userPlots.size()) {
                        getGui(finalPage + 1).open(event.getWhoClicked());
                    }
                });
            }

            if (configItem.getAction() == ConfigAction.CLOSE) {
                guiItem.setAction(event -> event.getWhoClicked().closeInventory());
            }
            gui.setItem(configItem.getSlot(), guiItem);
        }

        if (userPlots.isEmpty()) {
            return gui;
        }

        /* Plot Section */

        List<Plot> plots = new ArrayList<>(userPlots);
        int i = (page * plotsInGui);

        for (PlotMenuConfiguration.PlotSection plotSection : this.plotSelectorConfig.plotsPlaces.values()) {

            if (i >= userPlots.size()) {
                break;
            }

            Plot plot = plots.get(i);

            for (ConfigItem configItem : Arrays.asList(plotSection.optionsItem, plotSection.plotItem, plotSection.teleportItem)) {

                if (configItem == null) {
                    continue;
                }

                ConfigItem item = new ConfigItem(configItem.getSlot(), configItem.getMaterial(), configItem.getName(), configItem.getLore(), configItem.getAction());

                /* Create ItemStack */

                item = ConfigItem.replacePlotVariables(item, plot);
                ItemStack itemStack = item.getItemStack(this.miniMessage);

                /* Head Texture */

                if (SkullUtil.isPlayerSkull(itemStack)) {
                    itemStack = ItemBuilder.skull(itemStack)
                        .owner(Bukkit.getOfflinePlayer(plot.getOwner().getUser().getUuid()))
                        .build();
                }

                /* Create GuiItem */

                GuiItem guiItem = ItemBuilder.from(itemStack)
                    .name(miniMessage.deserialize(item.getName()).decoration(TextDecoration.ITALIC, false))
                    .asGuiItem();

                /* Create Events */

                this.guiActions.setupAction(configItem.getAction(), guiItem, plot);

                /* Add to Gui */

                gui.setItem(item.getSlot(), guiItem);
            }

            i++; // get new plot
        }

        return gui;
    }
}
