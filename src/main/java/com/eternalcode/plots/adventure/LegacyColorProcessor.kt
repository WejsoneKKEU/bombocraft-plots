package com.eternalcode.plots.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TextReplacementConfig
import java.util.function.UnaryOperator
import java.util.regex.MatchResult
import java.util.regex.Pattern

class LegacyColorProcessor : UnaryOperator<Component> {
    override fun apply(component: Component): Component {
        return component.replaceText { builder: TextReplacementConfig.Builder ->
            builder.match(Pattern.compile(".*"))
                .replacement { matchResult: MatchResult, builder1: TextComponent.Builder? ->
                    Legacy.Companion.component(
                        matchResult.group()
                    )
                }
        }
    }
}
