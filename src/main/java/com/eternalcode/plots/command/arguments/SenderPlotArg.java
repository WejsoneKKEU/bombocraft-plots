package com.eternalcode.plots.command.arguments;

import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.sugesstion.Suggestion;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Result;

import java.util.List;

@ArgumentName("senderPlot")
public class SenderPlotArg implements OneArgument<Plot> {

    private final PlotManager plotManager;
    private final UserManager userManager;

    public SenderPlotArg(PlotManager plotManager, UserManager userManager) {
        this.plotManager = plotManager;
        this.userManager = userManager;
    }

    @Override
    public Result<Plot, Object> parse(LiteInvocation invocation, String argument) {

        Player player = (Player) invocation.sender().getHandle();
        User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

        Option<Plot> plotOpt = this.plotManager.getPlot(argument);

        if (plotOpt.isEmpty()) {
            return Result.error("Plot with name '" + argument + "' was not found");
        }

        Plot plot = plotOpt.get();

        if (!plot.isMember(user)) {
            return Result.error("You are not member of plot '" + plot.getName() + "'");
        }

        return Result.ok(plot);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {

        Player player = (Player) invocation.sender().getHandle();
        User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

        return this.plotManager.getPlots(user)
            .stream()
            .map(Plot::getName)
            .map(String::toLowerCase)
            .map(Suggestion::of)
            .toList();
    }
}
