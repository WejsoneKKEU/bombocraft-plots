package com.eternalcode.plots.command

import com.eternalcode.plots.plot.PlotService
import dev.rollczi.litecommands.command.execute.Execute
import dev.rollczi.litecommands.command.route.Route
import org.bukkit.entity.Player

@Route(name = "plot")
class TestPlotCommand(private val plotService: PlotService) {
    @Execute(route = "create")
    fun create(player: Player) {
        val plotName = "TestPlot"
        val x = player.location.blockX
        val z = player.location.blockZ

        plotService.createPlot(plotName, x, z)
        player.sendMessage("Stworzono nową działkę o nazwie $plotName")
    }
}
