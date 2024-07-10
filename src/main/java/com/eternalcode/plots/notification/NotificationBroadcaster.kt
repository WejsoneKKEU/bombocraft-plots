package com.eternalcode.plots.notification

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.AudienceProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.function.BiConsumer

class NotificationBroadcaster(private val audienceProvider: AudienceProvider, private val miniMessage: MiniMessage) {
    fun send(commandSender: CommandSender, notificationType: NotificationType, text: String) {
        val audience = this.audience(commandSender)
        val component = miniMessage.deserialize(text)

        val handler = NOTIFICATION_HANDLERS[notificationType]
            ?: return

        handler.accept(audience, component)
    }

    fun sendMessage(commandSender: CommandSender, text: String) {
        val audience = this.audience(commandSender)

        audience.sendMessage(miniMessage.deserialize(text))
    }

    fun broadcast(commandSender: CommandSender?, text: String) {
        audienceProvider.all().sendMessage(miniMessage.deserialize(text))
    }

    private fun audience(sender: Any): Audience {
        if (sender is Player) {
            return audienceProvider.player(sender.uniqueId)
        }

        return audienceProvider.console()
    }

    companion object {
        private val NOTIFICATION_HANDLERS: Map<NotificationType, BiConsumer<Audience, Component>> = java.util.Map.of(
            NotificationType.CHAT, BiConsumer { obj: Audience, message: Component? ->
                obj.sendMessage(
                    message!!
                )
            },
            NotificationType.ACTION_BAR, BiConsumer { obj: Audience, message: Component? ->
                obj.sendActionBar(
                    message!!
                )
            },
            NotificationType.TITLE, BiConsumer { audience: Audience, component: Component? ->
                audience.showTitle(
                    Title.title(
                        component!!, Component.empty()
                    )
                )
            },
            NotificationType.SUBTITLE, BiConsumer { audience: Audience, component: Component? ->
                audience.showTitle(
                    Title.title(
                        Component.empty(), component!!
                    )
                )
            }
        )
    }
}
