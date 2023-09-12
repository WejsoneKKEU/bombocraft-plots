package com.eternalcode.plots.command;

import com.eternalcode.plots.configuration.implementations.commands.CommandsConfiguration;
import com.eternalcode.plots.utils.LegacyUtils;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.scheme.Scheme;
import org.bukkit.command.CommandSender;

public class InvalidUsageMessage implements InvalidUsageHandler<CommandSender> {

    private final CommandsConfiguration commandsConfiguration;

    public InvalidUsageMessage(CommandsConfiguration commandsConfiguration) {
        this.commandsConfiguration = commandsConfiguration;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation invocation, Scheme scheme) {

        String usageFormat = commandsConfiguration.usagePrefix;
        String usageCommand = commandsConfiguration.usageCommand;

        if (scheme.getSchemes().size() == 1) {
            commandSender.sendMessage(LegacyUtils.color(usageFormat + usageCommand.replace("{COMMAND}", scheme.getSchemes().get(0))));
            return;
        }
        commandSender.sendMessage(usageFormat);
        for (String usage : scheme.getSchemes()) {
            commandSender.sendMessage(LegacyUtils.color(usageCommand.replace("{COMMAND}", usage)));
        }
    }
}
