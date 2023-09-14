package com.eternalcode.plots.command.recoded.implementation;

import com.eternalcode.plots.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.notification.NotificationBroadcaster;
import com.eternalcode.plots.plot.old.PlotManager;
import com.eternalcode.plots.plot.recoded.member.PlotMemberService;
import com.eternalcode.plots.user.User;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.amount.Required;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

@Route(name = "plot kick")
@Permission("plots.command.kick")
public class KickCommand {

    private final PlotManager plotManager;
    private final NotificationBroadcaster notificationBroadcaster;
    private final MessageConfiguration messageConfiguration;
    private final PlotMemberService plotMemberService;

    public KickCommand(PlotManager plotManager, NotificationBroadcaster notificationBroadcaster, MessageConfiguration messageConfiguration, PlotMemberService plotMemberService) {
        this.plotManager = plotManager;
        this.notificationBroadcaster = notificationBroadcaster;
        this.messageConfiguration = messageConfiguration;
        this.plotMemberService = plotMemberService;
    }

    @Execute
    @Required(2)
    void kick(Player player, User user, @Arg User kicked, @Arg Plot plot) {
        Plot targetPlot = this.plotManager.getPlot(user, plot);

        if (targetPlot == null) {
            this.notificationBroadcaster.sendMessage(player, this.messageConfiguration.commands.needPlot);
            return;
        }

        if (!targetPlot.isOwner(user)) {
            this.notificationBroadcaster.sendMessage(player, this.messageConfiguration.commands.notOwner);
            return;
        }

        if (!this.plotMemberService.isMember(kicked)) {
            this.notificationBroadcaster.sendMessage(player, this.messageConfiguration.commands.notMember);
            return;
        }

        Formatter formatter = new Formatter()
            .register("{PLAYER}", kicked.getName())
            .register("{PLOT_NAME}", targetPlot.getName());

        this.plotManager.removeMember(targetPlot, kicked);
        this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.successKick));
    }

}
