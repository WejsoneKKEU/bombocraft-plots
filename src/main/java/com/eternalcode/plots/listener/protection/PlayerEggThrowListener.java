package com.eternalcode.plots.listener.protection;

import com.eternalcode.plots.configuration.implementation.ProtectionConfiguration;
import com.eternalcode.plots.plot.protection.ProtectionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class PlayerEggThrowListener implements Listener {

    private final ProtectionManager protectionManager;
    private final ProtectionConfiguration config;

    public PlayerEggThrowListener(ProtectionManager protectionManager, ProtectionConfiguration config) {
        this.protectionManager = protectionManager;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {

        if (!config.getGriefing().getEggThrow().isProtection()) return;

        if (this.protectionManager.hasBypass(event.getPlayer())) return;

        if (this.protectionManager.isAllowed(event.getPlayer(), event.getEgg().getLocation())) {
            return;
        }
        event.setHatching(false);
    }
}
