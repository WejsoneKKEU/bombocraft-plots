package com.eternalcode.plots.notgood.listener;

import com.eternalcode.plots.notgood.user.User;
import com.eternalcode.plots.notgood.user.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final UserManager userManager;

    public PlayerJoinListener(UserManager userManager) {
        this.userManager = userManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

        if (!user.getName().equalsIgnoreCase(player.getName())) {
            this.userManager.updateUsername(user, player.getName());
        }
    }

}
