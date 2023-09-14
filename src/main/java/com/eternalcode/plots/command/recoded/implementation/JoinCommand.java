package com.eternalcode.plots.command.recoded.implementation;

import com.eternalcode.plots.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.feature.invite.InviteManager;
import com.eternalcode.plots.notification.NotificationBroadcaster;
import com.eternalcode.plots.plot.recoded.member.PlotMemberService;
import com.eternalcode.plots.user.User;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.By;
import dev.rollczi.litecommands.command.amount.Required;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

@Route(name = "plot join")
@Permission("plots.command.join")
public class JoinCommand {

    private final InviteManager inviteManager;
    private final NotificationBroadcaster notificationBroadcaster;
    private final PlotMemberService plotMemberService;
    private final MessageConfiguration messageConfiguration;

    public JoinCommand(InviteManager inviteManager, NotificationBroadcaster notificationBroadcaster, PlotMemberService plotMemberService, MessageConfiguration messageConfiguration) {
        this.inviteManager = inviteManager;
        this.notificationBroadcaster = notificationBroadcaster;
        this.plotMemberService = plotMemberService;
        this.messageConfiguration = messageConfiguration;
    }

    @Required(1)
    @Execute
    void join(Player player, User user, @Arg @By("invite") Plot plot) {
        Formatter formatter = new Formatter()
            .register("{PLOT_NAME}", plot.getName());

        if (this.plotMemberService.isMember(user)) {
            this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.isAlreadyMember2));
            return;
        }

        if (!this.inviteManager.hasInvite(user, plot)) {
            this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.noInvite));
            return;
        }

        this.inviteManager.accept(user, plot);

        this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.successAcceptInvite));
    }
}
