package com.eternalcode.plots.notgood.command.recoded.implementation;

import com.eternalcode.plots.notgood.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.notgood.feature.invite.InviteManager;
import com.eternalcode.plots.good.member.MemberService;
import com.eternalcode.plots.good.notification.NotificationBroadcaster;
import com.eternalcode.plots.good.plot.Plot;
import com.eternalcode.plots.good.user.User;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.amount.Required;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

@Route(name = "plot invite")
@Permission("plots.command.invite")
public class InviteCommand {

    private final NotificationBroadcaster notificationBroadcaster;
    private final InviteManager inviteManager;
    private final MessageConfiguration messageConfiguration;
    private final MemberService memberService;

    public InviteCommand(NotificationBroadcaster notificationBroadcaster, InviteManager inviteManager, MessageConfiguration messageConfiguration, MemberService memberService) {
        this.notificationBroadcaster = notificationBroadcaster;
        this.inviteManager = inviteManager;
        this.messageConfiguration = messageConfiguration;
        this.memberService = memberService;
    }


    @Required(1)
    void invite(Player player, User user, @Arg User invitedUser, @Arg Plot targetPlot) {

        if (!targetPlot.isOwner(user)) {
            Formatter formatter = new Formatter()
                .register("{PLOT_NAME}", targetPlot.name());

            this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.notOwner));
            return;
        }

        if (this.memberService.isMember(targetPlot, invitedUser)) {
            Formatter formatter = new Formatter()
                .register("{PLOT_NAME}", targetPlot.name());

            this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.isAlreadyMember));
            return;
        }

        if (this.inviteManager.hasInvite(invitedUser, targetPlot)) {
            Formatter formatter = new Formatter()
                .register("{PLAYER}", invitedUser.name())
                .register("{PLOT_NAME}", targetPlot.name());

            this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.hasAlreadyInvite));
            return;
        }

        Formatter formatter = new Formatter()
            .register("{PLAYER}", invitedUser.name())
            .register("{PLOT_NAME}", targetPlot.name());

        this.inviteManager.create(invitedUser, targetPlot);
        this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.successInvite));

        // TODO: send invite message to invitedUser
    }

}
