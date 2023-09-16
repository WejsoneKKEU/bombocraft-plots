package com.eternalcode.plots.command;

import com.eternalcode.plots.plot.PlotService;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;

@Route(name = "plot")
public class TestPlotCommand {

    private final PlotService plotService;

    public TestPlotCommand(PlotService plotService) {
        this.plotService = plotService;
    }

    @Execute(route = "create")
    void create(Player player) {
        // Wpisz wartości wedle potrzeb
        String plotName = "TestPlot";
        Instant createdAt = Instant.now();
        Instant expireAt = createdAt.plus(Duration.ofDays(7)); // Na przykład +7 dni do czasu stworzenia
        int size = 10;
        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();

        this.plotService.createPlot(plotName, createdAt, expireAt, size, x, z);

        player.sendMessage("Stworzono nową działkę o nazwie " + plotName);
    }
}