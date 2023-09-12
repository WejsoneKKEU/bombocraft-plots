package com.eternalcode.plots.configuration.serializer;

import com.eternalcode.plots.configuration.implementations.gui.models.ConfigExtend;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class ConfigExtendSerializer implements ObjectSerializer<ConfigExtend> {

    @Override
    public boolean supports(@NonNull Class<? super ConfigExtend> type) {
        return ConfigExtend.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull ConfigExtend object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {

        if (object.items != null && !object.items.isEmpty()) {
            data.add("items", object.items);
        }

        if (object.money != 0) {
            data.add("money", object.money);
        }

        data.add("blocks", object.blocks);
    }

    @Override
    public ConfigExtend deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {

        Map<Material, Integer> items = data.containsKey("items")
            ? data.getAsMap("items", Material.class, Integer.class)
            : new HashMap<>();

        double money = data.containsKey("money")
            ? data.get("money", Double.class)
            : 0.00;

        int blocks = data.get("blocks", Integer.class);

        return new ConfigExtend(blocks, money, items);
    }
}
