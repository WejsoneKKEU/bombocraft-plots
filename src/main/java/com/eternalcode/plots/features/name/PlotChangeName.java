package com.eternalcode.plots.features.name;

import com.eternalcode.plots.EternalPlots;
import com.eternalcode.plots.configuration.implementations.LanguageConfiguration;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.utils.LegacyUtils;
import com.eternalcode.plots.utils.StringUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PlotChangeName {

    private final Plot plot;
    private final LanguageConfiguration lang;
    private final PlotManager plotManager;
    private final Plugin plugin;

    public PlotChangeName(Plot plot, LanguageConfiguration lang, PlotManager plotManager, Plugin plugin) {
        this.plot = plot;
        this.lang = lang;
        this.plotManager = plotManager;
        this.plugin = plugin;
    }

    public void sendGui(Player player) {

        AnvilGUI.Builder gui = new AnvilGUI.Builder();

        gui.onComplete((p, text) -> {
            if (!StringUtils.isLetterOrDigit(text)) {
                return AnvilGUI.Response.text(LegacyUtils.color(this.lang.plotCreation.anvilGui.illegalCharacters));
            }

            if (this.plotManager.isNameBusy(text)) {
                return AnvilGUI.Response.text(LegacyUtils.color(this.lang.plotCreation.anvilGui.nameExists));
            }

            if (text.length() <= 2 || text.length() >= 17) {
                return AnvilGUI.Response.text("Za długa/krótka nazwa");
            }

            this.plotManager.setName(this.plot, text);

            p.sendMessage(LegacyUtils.color(this.lang.plotCreation.anvilGui.nameChanged.replace("{NAME}", this.plot.getName())));

            return AnvilGUI.Response.close();
        });

        gui.itemLeft(new ItemStack(this.lang.plotCreation.anvilGui.guiItem));
        gui.text(LegacyUtils.color(this.plot.getName()));
        gui.title(LegacyUtils.color(this.lang.plotCreation.anvilGui.guiTitle));
        gui.plugin(this.plugin);
        gui.open(player);

    }

}
