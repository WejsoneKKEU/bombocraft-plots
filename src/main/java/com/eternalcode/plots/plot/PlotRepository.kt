package com.eternalcode.plots.plot

import java.util.*
import java.util.concurrent.CompletableFuture

interface PlotRepository {
    fun loadPlot(plotUUID: UUID?): CompletableFuture<Plot?>?

    fun savePlot(plot: Plot?)

    fun deletePlot(plot: Plot?)
}
