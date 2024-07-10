package com.eternalcode.plots.position

import org.bukkit.Bukkit
import org.bukkit.Location

class PositionAdapter private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        fun convert(location: Location): Position {
            checkNotNull(location.world) { "World is not defined" }

            return Position(
                location.x, location.y, location.z, location.yaw, location.pitch, location.world!!
                    .name
            )
        }

        fun convert(position: Position): Location {
            val world = Bukkit.getWorld(position.world)
                ?: throw IllegalStateException("World is not defined")

            return Location(world, position.x, position.y, position.z, position.yaw, position.pitch)
        }
    }
}
