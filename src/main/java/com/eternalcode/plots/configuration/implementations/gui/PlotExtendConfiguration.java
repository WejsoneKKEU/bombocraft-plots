package com.eternalcode.plots.configuration.implementations.gui;

import com.eternalcode.plots.configuration.implementations.gui.models.ConfigAction;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigBorder;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigFiller;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Map;

public class PlotExtendConfiguration extends OkaeriConfig {

    // TODO: system powiekszania dzialki

    @Comment({ "# Gui title" })
    public String title = "Extend Plot";

    @Comment({ "", "# Gui rows [1-6]" })
    public int rows = 5;

    @Comment({ "", "# Gui border materials" })
    public ConfigBorder border = new ConfigBorder(Material.GRAY_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE);

    @Comment({ "", "# Gui filler materials" })
    public ConfigFiller filler = new ConfigFiller();

    @Comment({ "", "Variables: ", "{PRIZE} - costs for plot extend", "{OLD_SIZE} - size before extend", "{NEW_SIZE} - size after extend" })
    public ConfigItem extendItem = new ConfigItem(22, Material.NETHERITE_INGOT, "&aPowiększ Działkę", Arrays.asList("&7Nowa wielkość: &a{NEW_SIZE}", "&7Cena: &6${PRIZE}"), ConfigAction.EXTEND_PLOT);

    @Comment({ "", "# Additional items in gui" })
    public Map<String, ConfigItem> items = new ImmutableMap.Builder<String, ConfigItem>()
        .build();
}
