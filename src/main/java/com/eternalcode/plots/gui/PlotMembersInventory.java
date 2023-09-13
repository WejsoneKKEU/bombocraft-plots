package com.eternalcode.plots.gui;

import com.eternalcode.plots.configuration.implementation.LanguageConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.PlotPlayersConfiguration;
import com.eternalcode.plots.configuration.implementation.gui.model.ConfigAction;
import com.eternalcode.plots.configuration.implementation.gui.model.ConfigItem;
import com.eternalcode.plots.notification.NotificationAnnouncer;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.plot.member.PlotMember;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.utilities.text.Formatter;

import java.util.List;

public class PlotMembersInventory {

    private final InventoryActions inventoryActions;
    private final PlotManager plotManager;
    private final PlotPlayersConfiguration plotPlayersConfiguration;
    private final MiniMessage miniMessage;
    private final NotificationAnnouncer notificationAnnouncer;
    private final Plot plot;
    private final UserManager userManager;
    private final LanguageConfiguration langConfig;

    public PlotMembersInventory(InventoryActions inventoryActions, PlotManager plotManager, PlotPlayersConfiguration plotPlayersConfiguration, MiniMessage miniMessage, NotificationAnnouncer notificationAnnouncer, Plot plot, UserManager userManager, LanguageConfiguration langConfig) {
        this.inventoryActions = inventoryActions;
        this.plotManager = plotManager;
        this.plotPlayersConfiguration = plotPlayersConfiguration;
        this.miniMessage = miniMessage;
        this.notificationAnnouncer = notificationAnnouncer;
        this.plot = plot;
        this.userManager = userManager;
        this.langConfig = langConfig;
    }

    public Gui createInventory() {
        String title = this.plotPlayersConfiguration.title;

        Formatter titleFormat = new Formatter()
            .register("{PLOT_NAME}", this.plot.getName());

        Gui gui = Gui.gui()
            .title(this.miniMessage.deserialize(titleFormat.format(title)))
            .rows(this.plotPlayersConfiguration.rows)
            .disableAllInteractions()
            .create();

        List<GuiItem> borderItems = this.plotPlayersConfiguration.border.getBorder();

        if (borderItems != null && !borderItems.isEmpty()) {
            for (GuiItem borderItem : borderItems) {
                gui.getFiller().fillBorder(borderItem);
            }
        }

        for (ConfigItem configItem : this.plotPlayersConfiguration.items.values()) {

            configItem = ConfigItem.replacePlotVariables(configItem, plot);
            GuiItem guiItem = new GuiItem(configItem.getItemStack(this.miniMessage));

            this.inventoryActions.setupAction(configItem.getAction(), guiItem, plot);
            gui.setItem(configItem.getSlot(), guiItem);
        }

        PlotPlayersConfiguration.PlayerTemplate playerTemplate = this.plotPlayersConfiguration.playerTemplate;

        for (PlotMember plotMember : plot.getMembers()) {
            User memberUser = plotMember.getUser();

            Formatter formatter = new Formatter()
                .register("{PLAYER}", memberUser.getName());

            List<Component> loreFormat = playerTemplate.itemLore.stream()
                .map(lore -> this.miniMessage.deserialize(formatter.format(lore)))
                .toList();

            ItemStack itemStack = ItemBuilder.skull()
                .owner(Bukkit.getOfflinePlayer(memberUser.getUuid()))
                .name(this.miniMessage.deserialize(formatter.format(playerTemplate.itemName)))
                .lore(loreFormat)
                .build();

            GuiItem playerGui = getGuiItem(plotMember, itemStack, playerTemplate);

            gui.addItem(playerGui);
        }

        List<GuiItem> fillerItems = this.plotPlayersConfiguration.filler.getFiller();

        if (fillerItems != null && !fillerItems.isEmpty()) {
            for (GuiItem fillerItem : fillerItems) {
                gui.getFiller().fill(fillerItem);
            }
        }

        return gui;
    }

    public void openInventory(Player player) {
        Gui gui = this.createInventory();
        gui.open(player);
    }


    private GuiItem getGuiItem(PlotMember plotMember, ItemStack itemStack, PlotPlayersConfiguration.PlayerTemplate playerTemplate) {
        GuiItem playerGui = new GuiItem(itemStack);

        playerGui.setAction(event -> {

            if (event.getClick().isLeftClick() && playerTemplate.leftClickAction == ConfigAction.KICK_PLAYER) {
                kickPlayer((Player) event.getViewers().get(0), this.plot, plotMember);
            }

            if (event.getClick().isRightClick() && playerTemplate.rightClickAction == ConfigAction.KICK_PLAYER) {
                kickPlayer((Player) event.getViewers().get(0), this.plot, plotMember);
            }
        });
        return playerGui;
    }

    private void kickPlayer(Player viewer, Plot plot, PlotMember plotMember) {
        User userSender = this.userManager.getOrCreate(viewer.getUniqueId(), viewer.getName());

        Formatter formatter = new Formatter()
            .register("{PLAYER}", plotMember.getUser().getName())
            .register("{PLOT_NAME}", plot.getName());

        if (!plot.isOwner(userSender)) {
            this.notificationAnnouncer.sendMessage(viewer, formatter.format(this.langConfig.commands.notOwner));
            return;
        }

        if (!plot.isMember(plotMember)) {
            this.notificationAnnouncer.sendMessage(viewer, formatter.format(this.langConfig.commands.notMember));
            return;
        }

        if (plotMember.getUser().equals(userSender)) {
            this.notificationAnnouncer.sendMessage(viewer, formatter.format(this.langConfig.commands.kickOwner));
            return;
        }

        this.plotManager.removeMember(plot, plotMember.getUser());
        this.notificationAnnouncer.sendMessage(viewer, formatter.format(this.langConfig.commands.successKick));
    }
}