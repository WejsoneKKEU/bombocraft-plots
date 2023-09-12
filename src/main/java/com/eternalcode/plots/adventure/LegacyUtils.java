package com.eternalcode.plots.adventure;

import dev.triumphteam.gui.components.util.Legacy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import panda.std.stream.PandaStream;

import java.util.List;

public class LegacyUtils {

    public static final MiniMessage MINIMESSAGE = MiniMessage.miniMessage();
    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private LegacyUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String color(String text) {
        return Legacy.SERIALIZER.serialize(MINIMESSAGE.deserialize(text));
    }

    public static Component legacyToComponent(String text) {
        return LEGACY_SERIALIZER.deserialize(text).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> legacyToComponent(Iterable<String> texts) {
        return PandaStream.of(texts).map(LegacyUtils::legacyToComponent).toList();
    }

}
