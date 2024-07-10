package com.eternalcode.plots.region

import org.bukkit.Location
import java.util.*
import kotlin.math.abs

@JvmRecord
data class Region(
    val regionUUID: UUID,
    val size: Int,
    val x: Int,
    val z: Int
) {
    fun withSize(size: Int): Region {
        return Region(this.regionUUID, size, this.x, this.z)
    }

    fun withX(x: Int): Region {
        return Region(this.regionUUID, this.size, x, this.z)
    }

    fun withZ(z: Int): Region {
        return Region(this.regionUUID, this.size, this.x, z)
    }

    fun isInRegion(location: Location): Boolean {
        val coordinateX = abs((location.blockX - this.x).toDouble()).toInt()
        val coordinateZ = abs((location.blockZ - this.z).toDouble()).toInt()

        return coordinateX <= this.size && coordinateZ <= this.size
    }
}
