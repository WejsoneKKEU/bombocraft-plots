package com.eternalcode.plots.listener.protection;

import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.plot.protection.ProtectionManager;
import com.eternalcode.plots.region.Region;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import panda.std.Option;

public class LingeringPotionSplashListener implements Listener {

    private final ProtectionManager protectionManager;
    private final ProtectionConfiguration config;
    private final PlotManager plotManager;

    public LingeringPotionSplashListener(ProtectionManager protectionManager, ProtectionConfiguration config, PlotManager plotManager) {
        this.protectionManager = protectionManager;
        this.config = config;
        this.plotManager = plotManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        if (!config.getGriefing().getNegativeEffects().isProtection()) {
            return;
        }

        ThrownPotion entity = event.getEntity();

        if (!this.protectionManager.isBadPotion(entity.getEffects())) {
            return;
        }

        Option<Region> regionOpt = this.plotManager.getRegion(entity.getLocation());

        if (regionOpt.isEmpty()) {
            return;
        }

        event.setCancelled(true);
    }
}
