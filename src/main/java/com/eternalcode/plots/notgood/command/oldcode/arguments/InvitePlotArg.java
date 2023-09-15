package com.eternalcode.plots.notgood.command.oldcode.arguments;

import com.eternalcode.plots.notgood.feature.invite.InviteManager;
import com.eternalcode.plots.notgood.plot.old.PlotManager;
import com.eternalcode.plots.good.user.User;
import com.eternalcode.plots.good.user.UserManager;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.std.Result;

import java.util.ArrayList;
import java.util.List;

@ArgumentName("senderPlot")
public class InvitePlotArg implements OneArgument<Plot> {

    private final PlotManager plotManager;
    private final UserManager userManager;
    private final InviteManager inviteManager;

    public InvitePlotArg(PlotManager plotManager, UserManager userManager, InviteManager inviteManager) {
        this.plotManager = plotManager;
        this.userManager = userManager;
        this.inviteManager = inviteManager;
    }

    @Override
    public Result<Plot, Object> parse(LiteInvocation invocation, String argument) {

        if (!(invocation.sender().getHandle() instanceof Player player)) {
            return Result.error("Command only for players");
        }

        User user = this.userManager.findOrCreate(player.getUniqueId(), player.getName());
        Option<Plot> plotOpt = this.plotManager.getPlot(argument);

        if (plotOpt.isEmpty()) {
            return Result.error("Plot with name '" + argument + "' was not found");
        }

        Plot plot = plotOpt.get();

        for (Plot invitePlot : this.inviteManager.getInviters(user)) {
            if (invitePlot.equals(plot)) {
                return Result.ok(plot);
            }
        }

        return Result.error("You are not invited to plot '" + plot.getName() + "'");
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {

        if (!(invocation.sender().getHandle() instanceof Player player)) {
            return new ArrayList<>();
        }

        User user = this.userManager.findOrCreate(player.getUniqueId(), player.getName());

        return this.inviteManager.getInviters(user)
            .stream()
            .map(Plot::getName)
            .map(String::toLowerCase)
            .map(Suggestion::of)
            .toList();
    }
}
