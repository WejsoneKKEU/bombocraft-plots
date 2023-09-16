package com.eternalcode.plots.user;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UserController implements Listener {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        User user = this.userManager.findOrCreate(player.getUniqueId(), player.getName());

        if (!user.name().equalsIgnoreCase(player.getName())) {
            this.userManager.updateName(user, player.getName());
        }
    }

}
