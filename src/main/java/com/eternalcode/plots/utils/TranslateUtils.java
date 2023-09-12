package com.eternalcode.plots.utils;

import org.bukkit.Material;

import java.util.Map;

// TODO: Remove feature for Translate item names.
public final class TranslateUtils {

    private TranslateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getTranslate(Material material, Map<String, String> map) {
        String itemName = material.name().toLowerCase();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(itemName)) {
                return entry.getValue();
            }
        }

        return itemName.replaceAll("_", " ");
    }

}
