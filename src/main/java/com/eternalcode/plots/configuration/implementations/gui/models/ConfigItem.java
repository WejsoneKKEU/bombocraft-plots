package com.eternalcode.plots.configuration.implementations.gui.models;

import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.utils.VariablesUtils;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigItem {

    @Getter
    public int slot;
    @Getter
    public ConfigAction action;
    @Getter
    public Material material;
    @Getter
    public String name;
    @Getter
    public List<String> lore;

    public ConfigItem(int slot, Material material, String name, List<String> lore, ConfigAction configAction) {
        this.slot = slot;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.action = configAction;
    }

    public ConfigItem() {

    }

    public static ConfigItem replacePlotVariables(ConfigItem configItem, Plot plot) {

        ConfigItem newConfigItem = new ConfigItem(configItem.slot, configItem.material, configItem.name, configItem.lore, configItem.action);
        newConfigItem.name = VariablesUtils.parsePlotVars(plot, configItem.name);
        newConfigItem.lore = VariablesUtils.parsePlotVars(plot, configItem.lore);
        return newConfigItem;
    }

    public ItemStack getItemStack(MiniMessage miniMessage) {

        List<String> lore = new ArrayList<>();
        List<Component> loreComponent = new ArrayList<>();

        if (this.lore != null && !this.lore.isEmpty()) {
            lore = this.lore;
        }

        for (String line : lore) {
            loreComponent.add(miniMessage.deserialize(line).decoration(TextDecoration.ITALIC, false));
        }

        return ItemBuilder.from(material)
            .name(miniMessage.deserialize(name).decoration(TextDecoration.ITALIC, false))
            .lore(loreComponent)
            .build();
    }
}
