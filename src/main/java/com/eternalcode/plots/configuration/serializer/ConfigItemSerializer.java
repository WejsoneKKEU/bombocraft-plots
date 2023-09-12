package com.eternalcode.plots.configuration.serializer;

import com.eternalcode.plots.configuration.implementations.gui.models.ConfigAction;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigItemSerializer implements ObjectSerializer<ConfigItem> {

    @Override
    public boolean supports(@NonNull Class<? super ConfigItem> type) {
        return ConfigItem.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(ConfigItem configItem, @NotNull SerializationData data, @NonNull GenericsDeclaration generics) {

        if (configItem.getMaterial() != null && configItem.getMaterial() != Material.AIR) {
            data.add("material", configItem.getMaterial());
        }

        if (configItem.getName() != null && !configItem.getName().equalsIgnoreCase("")) {
            data.add("name", configItem.getName());
        }

        if (configItem.getLore() != null && !configItem.getLore().isEmpty()) {
            data.add("lore", configItem.getLore());
        }

        if (configItem.getAction() != null && configItem.getAction() != ConfigAction.NONE) {
            data.add("action", configItem.getAction());
        }

        data.add("slot", configItem.getSlot());
    }

    @Override
    public ConfigItem deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {

        String materialName = data.get("material", String.class);
        Material material = Material.valueOf(materialName);

        String actionName = data.get("action", String.class);
        ConfigAction configAction;
        if (actionName == null) {
            configAction = ConfigAction.NONE;
        } else {
            configAction = ConfigAction.valueOf(actionName);
        }

        int slot = data.containsKey("slot")              // contains
            ? data.get("slot", Integer.class)       // yes
            : 0;                                        // no

        String name = data.containsKey("name")
            ? data.get("name", String.class)
            : "";

        List<String> lore = data.containsKey("lore")
            ? data.getAsList("lore", String.class)
            : new ArrayList<>();

        return new ConfigItem(slot, material, name, lore, configAction);
    }
}
