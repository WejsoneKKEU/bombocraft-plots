package com.eternalcode.plots.feature.name;

import com.eternalcode.plots.configuration.implementation.LanguageConfiguration;
import com.eternalcode.plots.notification.NotificationAnnouncer;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.util.recoded.TextUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import panda.utilities.text.Formatter;

import java.util.Collections;

public class PlotChangeName {

    private final Plot plot;
    private final LanguageConfiguration lang;
    private final PlotManager plotManager;
    private final Plugin plugin;
    private final NotificationAnnouncer notificationAnnouncer;

    public PlotChangeName(Plot plot, LanguageConfiguration lang, PlotManager plotManager, Plugin plugin, NotificationAnnouncer notificationAnnouncer) {
        this.plot = plot;
        this.lang = lang;
        this.plotManager = plotManager;
        this.plugin = plugin;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    public void sendGui(Player player) {
        AnvilGUI.Builder gui = new AnvilGUI.Builder()
            .text(this.plot.getName())
            .title(this.lang.plotCreation.changeNameInventory.guiTitle)
            .itemLeft(new ItemStack(this.lang.plotCreation.changeNameInventory.guiItem))
            .plugin(this.plugin)

            .onClick((slot, stateSnapshot) -> {
                String inputText = stateSnapshot.getText();

                if (!TextUtil.isLetterOrDigit(inputText)) {
                    this.notificationAnnouncer.sendMessage(player, this.lang.plotCreation.changeNameInventory.illegalCharacters);
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }

                if (inputText.length() <= 2 || inputText.length() >= 17) {
                    this.notificationAnnouncer.sendMessage(player, this.lang.plotCreation.changeNameInventory.nameTooLongOrShort);
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }

                if (this.plotManager.isNameBusy(inputText)) {
                    this.notificationAnnouncer.sendMessage(player, this.lang.plotCreation.changeNameInventory.nameExists);
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }

                this.plotManager.setName(this.plot, inputText);

                Formatter formatter = new Formatter()
                    .register("{NEW_NAME}", this.plot.getName());

                this.notificationAnnouncer.sendMessage(player, formatter.format(this.lang.plotCreation.changeNameInventory.nameChanged));

                return Collections.singletonList(AnvilGUI.ResponseAction.close());
            })

            .onClose(stateSnapshot -> player.sendMessage(this.lang.plotCreation.changeNameInventory.closed));

        gui.open(player);
    }

}
