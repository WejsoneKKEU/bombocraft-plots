package com.eternalcode.plots.notgood.command.recoded;

import com.eternalcode.plots.notgood.configuration.implementation.command.CommandsConfiguration;
import com.eternalcode.plots.good.notification.NotificationBroadcaster;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import java.util.List;

public class InvalidUsage implements InvalidUsageHandler<CommandSender> {

    private final CommandsConfiguration commandsConfiguration;
    private final NotificationBroadcaster announcer;

    public InvalidUsage(CommandsConfiguration commandsConfiguration, NotificationBroadcaster announcer) {
        this.commandsConfiguration = commandsConfiguration;
        this.announcer = announcer;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation invocation, Schematic scheme) {
        List<String> schematics = scheme.getSchematics();

        for (String schematic : schematics) {
            Formatter formatter = new Formatter()
                .register("{USAGE}", schematic);

            this.announcer.sendMessage(commandSender, formatter.format(this.commandsConfiguration.commandUsage));
        }
    }
}
