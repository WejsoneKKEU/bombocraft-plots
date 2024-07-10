package com.eternalcode.plots.position

import java.util.regex.Pattern

/**
 * Disclaimer - Bukkit [org.bukkit.Location] storage may cause a memory leak, because it is a wrapper for
 * coordinates and [org.bukkit.World] reference. If you need to store location use [Position] and
 * [PositionAdapter].
 */
@JvmRecord
data class Position(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float, val world: String) {
    val isNoneWorld: Boolean
        get() = this.world == NONE_WORLD

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }

        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val position = o as Position

        return (java.lang.Double.compare(
            position.x,
            x
        ) == 0 && java.lang.Double.compare(
            position.y,
            y
        ) == 0 && java.lang.Double.compare(position.z, this.z) == 0) && this.world == position.world
    }

    override fun toString(): String {
        return "Position{" +
                "x=" + this.x +
                ", y=" + this.y +
                ", z=" + this.z +
                ", yaw=" + this.yaw +
                ", pitch=" + this.pitch +
                ", world='" + this.world + '\'' +
                '}'
    }

    companion object {
        const val NONE_WORLD: String = "__NONE__"

        private val PARSE_FORMAT: Pattern =
            Pattern.compile("Position\\{x=(?<x>-?[\\d.]+), y=(?<y>-?[\\d.]+), z=(?<z>-?[\\d.]+), yaw=(?<yaw>-?[\\d.]+), pitch=(?<pitch>-?[\\d.]+), world='(?<world>.+)'}")

        fun parse(parse: String): Position {
            val matcher = PARSE_FORMAT.matcher(parse)

            require(matcher.find()) { "Invalid position format: $parse" }

            return Position(
                matcher.group("x").toDouble(),
                matcher.group("y").toDouble(),
                matcher.group("z").toDouble(),
                matcher.group("yaw").toFloat(),
                matcher.group("pitch").toFloat(),
                matcher.group("world")
            )
        }
    }
}
