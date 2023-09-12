package com.eternalcode.plots.listener.protection;

import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.plot.protection.ProtectionManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishListener implements Listener {

    private final ProtectionManager protectionManager;
    private final ProtectionConfiguration config;

    public PlayerFishListener(ProtectionManager protectionManager, ProtectionConfiguration config) {
        this.protectionManager = protectionManager;
        this.config = config;
    }

    @EventHandler
    public void onPlayerHitFishingrodscorpion(final PlayerFishEvent event) {
        if (!config.getGriefing().getFishingRod().isProtection()) {
            return;
        }

        Entity caught = event.getCaught();

        if (caught == null) {
            return;
        }

        Player player = event.getPlayer();

        if (this.protectionManager.hasBypass(player)) return;

        if (this.protectionManager.isAllowed(player, caught.getLocation())) {
            return;
        }

        event.setCancelled(true);
    }
}
