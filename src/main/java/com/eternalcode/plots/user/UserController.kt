package com.eternalcode.plots.user

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class UserController(private val userService: UserService) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val user = userService.findOrCreate(player.uniqueId)
        user.updateName(player.name)
    }
}
