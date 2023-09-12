package com.eternalcode.plots.gui;

import com.eternalcode.plots.configuration.ConfigurationManager;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigAction;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigExtend;
import com.eternalcode.plots.features.extend.CostsService;
import com.eternalcode.plots.features.extend.PlotExtend;
import com.eternalcode.plots.features.name.PlotChangeName;
import com.eternalcode.plots.hook.VaultProvider;
import com.eternalcode.plots.notification.NotificationAnnouncer;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.position.PositionAdapter;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import io.papermc.lib.PaperLib;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

class GuiActions {

    private final VaultProvider vaultProvider;
    private final PlotManager plotManager;
    private final UserManager userManager;
    private final GuiFactory guiFactory;
    private final ConfigurationManager configurationManager;
    private final Plugin plugin;
    private final NotificationAnnouncer notificationAnnouncer;

    GuiActions(GuiFactory guiFactory, VaultProvider vaultProvider, PlotManager plotManager, UserManager userManager, ConfigurationManager configurationManager, Plugin plugin, NotificationAnnouncer notificationAnnouncer) {
        this.vaultProvider = vaultProvider;
        this.plotManager = plotManager;
        this.userManager = userManager;
        this.guiFactory = guiFactory;
        this.configurationManager = configurationManager;
        this.plugin = plugin;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    void setupAction(ConfigAction action, GuiItem guiItem, Plot plot) {
        guiItem.setAction(getAction(action, plot));
    }

    GuiAction<InventoryClickEvent> getAction(ConfigAction action, Plot plot) {
        return switch (action) {

            /* All */
            case NONE -> null;
            case CLOSE -> this.closeGui();
            case KICK_PLAYER -> null;

            /* Plot Selector */
            case PREVIOUS_PAGE, NEXT_PAGE -> null;

            /* Menus */
            case PLOT_MENU -> this.openPlotPanel(plot);
            case NAME_MENU -> this.openChangeNamePanel(plot);
            case FLAGS_MENU -> this.openFlagsPanel(plot);
            case EXTEND_MENU -> this.openExtendPanel(plot);
            case PLAYERS_MENU -> this.openPlayersPanel(plot);

            /* Plot Functions */
            case TELEPORT_PLOT -> this.teleportPlot(plot);
            case EXTEND_PLOT -> this.extendPlot(plot);
        };
    }

    private GuiAction<InventoryClickEvent> openPlayersPanel(Plot plot) {
        return event -> {

            Player player = (Player) event.getWhoClicked();
            User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

            if (!plot.isOwner(user)) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Nie jesteś właścicielem działki");
                return;
            }

            this.guiFactory.createPlotPlayersGui(plot).getGui().open(event.getWhoClicked());
        };
    }

    private GuiAction<InventoryClickEvent> extendPlot(Plot plot) {
        return event -> {

            ConfigExtend configExtend = ConfigExtend.getConfigExtend(plot, this.configurationManager.getPluginConfiguration());

            if (configExtend == null) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Osiągnąłeś już limit ulepszeń działki");
                return;
            }

            int newSize = plot.getRegion().getSize() + configExtend.blocks;
            Player player = (Player) event.getWhoClicked();

            PlotExtend plotExtend = PlotExtend.create(plot, this.vaultProvider, this.plotManager, this.configurationManager.getPluginConfiguration());

            if (plotExtend == null) {
                player.sendMessage(ChatColor.RED + "Limit wielkości działki został już osiągnięty");
                return;
            }

            CostsService costsService = plotExtend.getCostsService();

            if (plotExtend.isLimit(newSize)) {
                player.sendMessage(ChatColor.RED + "Limit wielkości działki został już osiągnięty");
                return;
            }

            if (!costsService.hasCosts(player)) {
                player.sendMessage(costsService.noCostsMessage());
                return;
            }

            costsService.removeCosts(player);
            plotExtend.extendPlot(plot);

            player.sendMessage(ChatColor.GREEN + "Powiększono działkę na " + newSize);

            PlotExtend plotExtendAfter = PlotExtend.create(plot, this.vaultProvider, this.plotManager, this.configurationManager.getPluginConfiguration());

            if (plotExtendAfter == null) {
                player.closeInventory();
                return;
            }

            this.guiFactory.createPlotExtendGui(plot).getGui().open(player);
        };
    }

    private GuiAction<InventoryClickEvent> teleportPlot(Plot plot) {
        return event -> {

            Player player = (Player) event.getWhoClicked();

            PaperLib.teleportAsync(player, PositionAdapter.convert(plot.getRegion().getCenter()).add(
                0.5,
                1,
                0.5
            ));
        };
    }

    private GuiAction<InventoryClickEvent> openFlagsPanel(Plot plot) {
        return event -> {

            Player player = (Player) event.getWhoClicked();
            User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

            if (!plot.isOwner(user)) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Nie jesteś właścicielem działki");
                return;
            }

            this.guiFactory.createPlotFlagsGui(plot).getGui().open(event.getWhoClicked());
        };
    }

    private GuiAction<InventoryClickEvent> openExtendPanel(Plot plot) {
        return event -> {

            Player player = (Player) event.getWhoClicked();
            User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

            if (!plot.isOwner(user)) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Nie jesteś właścicielem działki");
                return;
            }

            if (ConfigExtend.getConfigExtend(plot, this.configurationManager.getPluginConfiguration()) == null) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Osiągnąłeś już limit ulepszeń działki");
                return;
            }

            this.guiFactory.createPlotExtendGui(plot).getGui().open(event.getWhoClicked());
        };
    }

    private GuiAction<InventoryClickEvent> openChangeNamePanel(Plot plot) {
        return event -> {

            Player player = (Player) event.getWhoClicked();
            User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

            if (!plot.isOwner(user)) {
                player.sendMessage(ChatColor.RED + "Nie jesteś właścicielem działki");
                return;
            }

            new PlotChangeName(plot, this.configurationManager.getLanguageConfiguration(), this.plotManager, this.plugin, this.notificationAnnouncer)
                .sendGui((Player) event.getWhoClicked());
        };
    }

    private GuiAction<InventoryClickEvent> openPlotPanel(Plot plot) {
        return event -> this.guiFactory.createPlotPanelGui(plot).getGui().open(event.getWhoClicked());
    }

    private GuiAction<InventoryClickEvent> closeGui() {
        return event -> event.getWhoClicked().closeInventory();
    }
}
