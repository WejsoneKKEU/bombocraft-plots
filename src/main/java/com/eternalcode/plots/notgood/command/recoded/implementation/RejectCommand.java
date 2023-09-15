package com.eternalcode.plots.notgood.command.recoded.implementation;

import com.eternalcode.plots.notgood.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.notgood.feature.invite.InviteManager;
import com.eternalcode.plots.good.notification.NotificationBroadcaster;
import com.eternalcode.plots.good.user.User;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.By;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

@Route(name = "plot reject")
@Permission("plots.command.reject")
public class RejectCommand {

    private final InviteManager inviteManager;
    private final NotificationBroadcaster notificationBroadcaster;
    private final MessageConfiguration messageConfiguration;

    public RejectCommand(InviteManager inviteManager, NotificationBroadcaster notificationBroadcaster, MessageConfiguration messageConfiguration) {
        this.inviteManager = inviteManager;
        this.notificationBroadcaster = notificationBroadcaster;
        this.messageConfiguration = messageConfiguration;
    }

    @Execute
    public void reject(Player player, User user, @Arg @By("invite") Plot plot) {
        Formatter formatter = new Formatter()
            .register("{PLOT_NAME}", plot.getName());

        if (!this.inviteManager.hasInvite(user, plot)) {
            this.notificationBroadcaster.sendMessage(player, formatter.format(messageConfiguration.commands.noInvite));
            return;
        }

        this.inviteManager.decline(user, plot);
        this.notificationBroadcaster.sendMessage(player, formatter.format(messageConfiguration.commands.successRejectInvite));

        // TODO: Send message to plot owner that player rejected invite
    }

}
