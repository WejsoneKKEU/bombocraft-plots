package com.eternalcode.plots.command.arguments;

import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import dev.rollczi.litecommands.argument.ArgumentName;
import dev.rollczi.litecommands.argument.simple.OneArgument;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.sugesstion.Suggestion;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import panda.std.Result;

import java.util.List;

@ArgumentName("user")
public class UserArg implements OneArgument<User> {

    private final Server server;
    private final UserManager userManager;

    public UserArg(Server server, UserManager userManager) {
        this.server = server;
        this.userManager = userManager;
    }

    @Override
    public Result<User, Object> parse(LiteInvocation invocation, String argument) {
        Player player = this.server.getPlayer(argument);

        if (player == null) {
            return Result.error("Gracz nie jest online");
        }

        return Result.ok(this.userManager.getOrCreate(player.getUniqueId(), player.getName()));
    }

    @Override
    public List<Suggestion> suggest(LiteInvocation invocation) {
        return this.server.getOnlinePlayers().stream()
            .map(HumanEntity::getName)
            .map(Suggestion::of)
            .toList();
    }
}
