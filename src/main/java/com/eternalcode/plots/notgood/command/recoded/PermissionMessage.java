package com.eternalcode.plots.notgood.command.recoded;

import com.eternalcode.plots.notgood.configuration.implementation.MessageConfiguration;
import com.eternalcode.plots.good.notification.NotificationBroadcaster;
import com.google.common.base.Joiner;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private final MessageConfiguration messages;
    private final NotificationBroadcaster broadcaster;

    public PermissionMessage(MessageConfiguration messages, NotificationBroadcaster broadcaster) {
        this.messages = messages;
        this.broadcaster = broadcaster;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        String value = Joiner.on(", ")
            .join(requiredPermissions.getPermissions());

        Formatter formatter = new Formatter()
            .register("{PERMISSION}", value);

        this.broadcaster.sendMessage(commandSender, formatter.format(this.messages.warnings.noPermission));
    }
}
