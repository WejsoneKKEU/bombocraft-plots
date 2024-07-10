package com.eternalcode.plots.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import panda.std.stream.PandaStream

class Legacy private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val SECTION_SERIALIZER: LegacyComponentSerializer = LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build()

        private val AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build()

        fun component(text: String?): Component {
            return AMPERSAND_SERIALIZER.deserialize(text!!)
        }

        fun component(texts: Iterable<String>?): List<Component> {
            return PandaStream.of(texts).map { text: String? -> component(text) }
                .toList()
        }
    }
}
