package com.eternalcode.plots.gui;

import com.eternalcode.plots.configuration.implementations.LanguageConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.PlotPlayersConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigAction;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import com.eternalcode.plots.plot.Member;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotHelper;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import com.eternalcode.plots.utils.LegacyUtils;
import com.eternalcode.plots.utils.VariablesUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

public class PlotPanelPlayersGui implements GuiProvider {

    private final GuiActions guiActions;
    private final PlotManager plotManager;
    private final PlotPlayersConfiguration plotPlayersConfiguration;
    private final MiniMessage miniMessage;
    private final Plot plot;
    private final UserManager userManager;
    private final LanguageConfiguration langConfig;

    PlotPanelPlayersGui(GuiActions guiActions, PlotManager plotManager, PlotPlayersConfiguration plotPlayersConfiguration, MiniMessage miniMessage, Plot plot, UserManager userManager, LanguageConfiguration langConfig) {
        this.guiActions = guiActions;
        this.plotManager = plotManager;
        this.plotPlayersConfiguration = plotPlayersConfiguration;
        this.miniMessage = miniMessage;
        this.plot = plot;
        this.userManager = userManager;
        this.langConfig = langConfig;
    }

    @Override
    public Gui getGui() {
        String title = this.plotPlayersConfiguration.title;

        Gui gui = Gui.gui()
            .title(this.miniMessage.deserialize(VariablesUtils.parsePlotVars(this.plot, title)))
            .rows(this.plotPlayersConfiguration.rows)
            .disableAllInteractions()
            .create();

        if (this.plotPlayersConfiguration.border.getBorder().isPresent()) {
            gui.getFiller().fillBorder(this.plotPlayersConfiguration.border.getBorder().get());
        }

        /* Items */

        for (ConfigItem configItem : this.plotPlayersConfiguration.items.values()) {

            configItem = ConfigItem.replacePlotVariables(configItem, plot);
            GuiItem guiItem = new GuiItem(configItem.getItemStack(this.miniMessage));

            this.guiActions.setupAction(configItem.getAction(), guiItem, plot);

            gui.setItem(configItem.getSlot(), guiItem);
        }

        /* Players */

        PlotPlayersConfiguration.PlayerTemplate playerTemplate = this.plotPlayersConfiguration.playerTemplate;

        for (Member member : plot.getMembers()) {

            User memberUser = member.getUser();

            ItemStack itemStack = ItemBuilder.skull()

                .owner(Bukkit.getOfflinePlayer(memberUser.getUuid()))
                .name(LegacyUtils.legacyToComponent(playerTemplate.itemName.replace("{PLAYER_NAME}", memberUser.getName())))
                .lore(LegacyUtils.legacyToComponent(
                    VariablesUtils.parseVariable(playerTemplate.itemLore, "{PLAYER_NAME}", memberUser.getName())
                ))
                .build();

            GuiItem playerGui = new GuiItem(itemStack);

            playerGui.setAction(event -> {

                if (event.getClick().isLeftClick() && playerTemplate.leftClickAction == ConfigAction.KICK_PLAYER) {
                    kickPlayer((Player) event.getViewers().get(0), this.plot, member);
                } else if (event.getClick().isRightClick() && playerTemplate.rightClickAction == ConfigAction.KICK_PLAYER) {
                    kickPlayer((Player) event.getViewers().get(0), this.plot, member);
                }

            });

            gui.addItem(playerGui);
        }

        if (this.plotPlayersConfiguration.filler.getFiller().isPresent()) {
            gui.getFiller().fill(this.plotPlayersConfiguration.filler.getFiller().get());
        }

        return gui;
    }

    private void kickPlayer(Player viewer, Plot plot, Member member) {

        User userSender = this.userManager.getOrCreate(viewer.getUniqueId(), viewer.getName());

        if (!plot.isOwner(userSender)) {
            viewer.sendMessage(PlotHelper.formatMessage(langConfig.commands.notOwner, userSender, Option.of(plot)));
            return;
        }

        if (!plot.isMember(member)) {
            viewer.sendMessage(PlotHelper.formatMessage(langConfig.commands.notMember, member.getUser(), Option.of(plot)));
            return;
        }

        if (member.getUser().equals(userSender)) {
            viewer.sendMessage(PlotHelper.formatMessage(langConfig.commands.kickOwner, member.getUser(), Option.of(plot)));
            return;
        }

        this.plotManager.removeMember(plot, member.getUser());
        viewer.sendMessage(PlotHelper.formatMessage(langConfig.commands.successKick, member.getUser().getName(), Option.of(plot)));
    }
}
