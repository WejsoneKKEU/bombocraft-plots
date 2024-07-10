package com.eternalcode.plots.plot

import com.eternalcode.plots.region.RegionService
import org.bukkit.Location
import java.time.Instant

class PlotService(
    private val regionService: RegionService,
    private val settings: PlotSettings,
    private val plotRepository: PlotRepository
) {
    private val plots: MutableSet<Plot> = HashSet()

    fun createPlot(name: String, x: Int, z: Int) {
        val createdAt = Instant.now()
        val expireAt = createdAt.plus(settings.expire)

        val regionId = regionService.createRegion(x, z)
        val plot = Plot(regionId, name, createdAt, expireAt)

        plots.add(plot)
    }

    fun getPlotForLocation(location: Location?): Plot? {
        for (plot in plots) {
            val region = regionService.getRegion(plot.plotId)
            if (region != null && region.isInRegion(location!!)) {
                return plot
            }
        }
        return null
    }
}
