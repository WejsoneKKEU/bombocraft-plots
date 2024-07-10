package com.eternalcode.plots

import com.eternalcode.plots.adventure.LegacyColorProcessor
import com.eternalcode.plots.command.TestPlotCommand
import com.eternalcode.plots.plot.PlotService
import com.eternalcode.plots.region.RegionService
import com.eternalcode.plots.scheduler.BukkitSchedulerImpl
import com.eternalcode.plots.scheduler.Scheduler
import com.google.common.base.Stopwatch
import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import dev.rollczi.litecommands.bukkit.tools.BukkitOnlyPlayerContextual
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bstats.bukkit.Metrics
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.time.Duration
import java.util.concurrent.TimeUnit

class PlotsPlugin : JavaPlugin() {
    private var audienceProvider: BukkitAudiences? = null
    private var miniMessage: MiniMessage? = null
    private var scheduler: Scheduler? = null

    private var liteCommands: LiteCommands<CommandSender>? = null

    override fun onEnable() {
        val started = Stopwatch.createStarted()
        val server = this.server

        this.audienceProvider = BukkitAudiences.create(this)
        this.miniMessage = MiniMessage.builder()
            .postProcessor(LegacyColorProcessor())
            .build()

        this.scheduler = BukkitSchedulerImpl(this)


        val regionService = RegionService({ 20 }, null)
        val plotService = PlotService(regionService, { Duration.ofHours(1) }, null)

        this.liteCommands = LiteBukkitFactory.builder(server, "eternalplots")
            .contextualBind(Player::class.java, BukkitOnlyPlayerContextual("this command only for players"))
            .commandInstance(
                TestPlotCommand(plotService)
            )
            .register()

        /*
        Stream.of(
            new TestingPlotController(plotService)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
*/
        Metrics(this, 19792)

        val millis = started.elapsed(TimeUnit.MILLISECONDS)
        logger.info("Successfully loaded EternalPlots in " + millis + "ms")
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
