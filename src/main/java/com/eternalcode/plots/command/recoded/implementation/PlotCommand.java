package com.eternalcode.plots.command.recoded.implementation;

import com.eternalcode.plots.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.notification.NotificationBroadcaster;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

@Route(name = "plot")
@Permission("plots.command.plot")
public class PlotCommand {

    private final NotificationBroadcaster announcer;
    private final MessageConfiguration language;

    public PlotCommand(NotificationBroadcaster announcer, MessageConfiguration language) {
        this.announcer = announcer;
        this.language = language;
    }

    @Execute
    void help(Player player) {
        for (String message : this.language.help.message) {
            this.announcer.sendMessage(player, message);
        }
    }
}
