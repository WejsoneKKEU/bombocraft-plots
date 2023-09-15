package com.eternalcode.plots.notgood.command.oldcode.arguments;

import com.eternalcode.plots.notgood.plot.old.PlotManager;
import com.eternalcode.plots.notgood.plot.recoded.member.PlotMemberService;
import com.eternalcode.plots.good.user.User;
import com.eternalcode.plots.good.user.UserManager;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Result;

import java.util.List;

@ArgumentName("senderPlot")
public class SenderPlotArg implements OneArgument<Plot> {

    private final PlotManager plotManager;
    private final PlotMemberService plotMemberService;
    private final UserManager userManager;

    public SenderPlotArg(PlotManager plotManager, PlotMemberService plotMemberService, UserManager userManager) {
        this.plotManager = plotManager;
        this.plotMemberService = plotMemberService;
        this.userManager = userManager;
    }

    @Override
    public Result<Plot, Object> parse(LiteInvocation invocation, String argument) {

        Player player = (Player) invocation.sender().getHandle();
        User user = this.userManager.findOrCreate(player.getUniqueId(), player.getName());

        Option<Plot> plotOpt = this.plotManager.getPlot(argument);

        if (plotOpt.isEmpty()) {
            return Result.error("Plot with name '" + argument + "' was not found");
        }

        Plot plot = plotOpt.get();

        if (!this.plotMemberService.isMember(user)) {
            return Result.error("You are not member of plot '" + plot.getName() + "'");
        }

        return Result.ok(plot);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {

        Player player = (Player) invocation.sender().getHandle();
        User user = this.userManager.findOrCreate(player.getUniqueId(), player.getName());

        return this.plotManager.getPlots(user)
            .stream()
            .map(Plot::getName)

            // TODO: why this is toLowerCase?
            .map(String::toLowerCase)
            .map(Suggestion::of)
            .toList();
    }
}
