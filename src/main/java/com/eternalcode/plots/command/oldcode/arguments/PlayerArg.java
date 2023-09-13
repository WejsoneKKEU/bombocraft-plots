package com.eternalcode.plots.command.oldcode.arguments;

import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Result;

import java.util.List;

@ArgumentName("player")
public class PlayerArg implements OneArgument<Player> {

    private final Server server;

    public PlayerArg(Server server) {
        this.server = server;
    }

    @Override
    public Result<Player, Object> parse(LiteInvocation invocation, String argument) {
        Player player = this.server.getPlayer(argument);

        if (player == null) {
            return Result.error(ChatColor.RED + "Gracz nie jest online");
        }

        return Result.ok(player);
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return OneArgument.super.suggest(invocation);
    }
}
