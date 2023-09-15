package com.eternalcode.plots.good.user;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserController implements Listener {

    private final UserManager userManager;
    private final UserRepository userRepository;

    public UserController(UserManager userManager, UserRepository userRepository) {
        this.userManager = userManager;
        this.userRepository = userRepository;
    }

    @EventHandler(ignoreCancelled = true)
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        User user = this.userManager.findOrCreate(player.getUniqueId(), player.getName());

        if (!user.name().equalsIgnoreCase(player.getName())) {
            this.userManager.updateName(user, player.getName());
        }
    }

/*    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.userRepository.saveUser(this.userManager.findOrCreate(player.getUniqueId(), player.getName()));
    }*/

}
