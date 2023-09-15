package com.eternalcode.plots.notgood.command.recoded.implementation;

import com.eternalcode.plots.notgood.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.good.notification.NotificationBroadcaster;
import com.eternalcode.plots.notgood.plot.old.PlotManager;
import com.eternalcode.plots.good.user.User;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.By;
import dev.rollczi.litecommands.command.amount.Required;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

@Route(name = "plot delete")
@Permission("plots.command.delete")
public class DeleteCommand {

    private final MessageConfiguration messageConfiguration;
    private final NotificationBroadcaster notificationBroadcaster;
    private final PlotManager plotManager;

    public DeleteCommand(MessageConfiguration messageConfiguration, NotificationBroadcaster notificationBroadcaster, PlotManager plotManager) {
        this.messageConfiguration = messageConfiguration;
        this.notificationBroadcaster = notificationBroadcaster;
        this.plotManager = plotManager;
    }

    @Required(1)
    public void delete(Player player, User user, @Arg @By("sender") Plot plot) {
        Formatter formatter = new Formatter()
            .register("{PLOT_NAME}", plot.getName());

        if (!plot.isOwner(user)) {
            this.notificationBroadcaster.sendMessage(player, formatter.format(this.messageConfiguration.commands.notOwner));
            return;
        }

        this.plotManager.delete(plot);
    }
}
