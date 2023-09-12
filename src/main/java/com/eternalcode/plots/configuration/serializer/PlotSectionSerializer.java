package com.eternalcode.plots.configuration.serializer;

import com.eternalcode.plots.configuration.implementations.gui.PlotMenuConfiguration;
import com.eternalcode.plots.configuration.implementations.gui.models.ConfigItem;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class PlotSectionSerializer implements ObjectSerializer<PlotMenuConfiguration.PlotSection> {

    @Override
    public boolean supports(@NonNull Class<? super PlotMenuConfiguration.PlotSection> type) {
        return PlotMenuConfiguration.PlotSection.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(PlotMenuConfiguration.PlotSection plotSection, @NotNull SerializationData data, @NonNull GenericsDeclaration generics) {

        if (plotSection.plotItem != null) {
            data.add("plotItem", plotSection.plotItem);
        }

        if (plotSection.optionsItem != null) {
            data.add("optionsItem", plotSection.optionsItem);
        }

        if (plotSection.teleportItem != null) {
            data.add("teleportItem", plotSection.teleportItem);
        }
    }

    @Override
    public PlotMenuConfiguration.PlotSection deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {

        ConfigItem plotItem = data.containsKey("plotItem")
            ? data.get("plotItem", ConfigItem.class)
            : null;

        ConfigItem optionsItem = data.containsKey("optionsItem")
            ? data.get("optionsItem", ConfigItem.class)
            : null;

        ConfigItem teleportItem = data.containsKey("teleportItem")
            ? data.get("teleportItem", ConfigItem.class)
            : null;

        return new PlotMenuConfiguration.PlotSection(plotItem, optionsItem, teleportItem);
    }
}
