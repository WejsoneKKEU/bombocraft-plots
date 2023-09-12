package com.eternalcode.plots.command.contextual;

import dev.rollczi.litecommands.command.Invocation;
import dev.rollczi.litecommands.contextual.Contextual;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.std.Result;

public class PlayerContextual implements Contextual<CommandSender, Player> {

    @Override
    public Result<Player, Object> extract(CommandSender commandSender, Invocation<CommandSender> invocation) {
        if (!(invocation.sender().getHandle() instanceof Player player)) {
            return Result.error("&cCommand sender must be a player!");
        }

        return Result.ok(player);
    }
}
