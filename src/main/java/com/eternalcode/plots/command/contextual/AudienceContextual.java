package com.eternalcode.plots.command.contextual;

import dev.rollczi.litecommands.command.Invocation;
import dev.rollczi.litecommands.contextual.Contextual;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import panda.std.Result;

public class AudienceContextual implements Contextual<CommandSender, Audience> {

    private final BukkitAudiences bukkitAudiences;

    public AudienceContextual(BukkitAudiences bukkitAudiences) {
        this.bukkitAudiences = bukkitAudiences;
    }

    @Override
    public Result<Audience, Object> extract(CommandSender commandSender, Invocation<CommandSender> invocation) {
        Object sender = invocation.sender().getHandle();

        if (sender instanceof Player player) {
            return Result.ok(this.bukkitAudiences.player(player));
        }

        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender || sender instanceof BlockCommandSender) {
            return Result.ok(this.bukkitAudiences.console());
        }

        return Result.error("Unsupported sender type: " + sender.getClass().getName());
    }
}
