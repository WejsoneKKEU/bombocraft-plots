package com.eternalcode.plots.command.arguments;

import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.sugesstion.Suggestion;
import panda.std.Option;
import panda.std.Result;

import java.util.List;


@ArgumentName("plot")
public class PlotArg implements OneArgument<Plot> {

    private final PlotManager plotManager;

    public PlotArg(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    @Override
    public Result<Plot, Object> parse(LiteInvocation invocation, String argument) {

        Option<Plot> plotOpt = this.plotManager.getPlot(argument);

        if (plotOpt.isEmpty()) {
            return Result.error("Plot with name '" + argument + "' was not found");
        }

        return Result.ok(plotOpt.get());
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return this.plotManager.getPlots()
            .stream()
            .map(Plot::getName)
            .map(String::toLowerCase)
            .map(Suggestion::of)
            .toList();
    }
}
