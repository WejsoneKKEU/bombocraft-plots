package com.eternalcode.plots.notgood.command.recoded.implementation;

import com.eternalcode.plots.notgood.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.notgood.feature.border.BorderTask;
import com.eternalcode.plots.good.notification.NotificationBroadcaster;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

@Route(name = "plot border")
@Permission("plots.command.border")
public class BorderCommand {

    private final BorderTask borderTask;
    private final MessageConfiguration messageConfiguration;
    private final NotificationBroadcaster notificationBroadcaster;

    public BorderCommand(BorderTask borderTask, MessageConfiguration messageConfiguration, NotificationBroadcaster notificationBroadcaster) {
        this.borderTask = borderTask;
        this.messageConfiguration = messageConfiguration;
        this.notificationBroadcaster = notificationBroadcaster;
    }

    @Execute
    void border(Player player) {
        if (!this.borderTask.hasShowed(player)) {
            this.borderTask.show(player);
            this.notificationBroadcaster.sendMessage(player, this.messageConfiguration.commands.showBorder);
            return;
        }

        this.borderTask.hide(player);
        this.notificationBroadcaster.sendMessage(player, this.messageConfiguration.commands.hideBorder);
    }

}
