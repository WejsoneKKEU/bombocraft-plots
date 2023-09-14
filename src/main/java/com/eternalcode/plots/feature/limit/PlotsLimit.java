package com.eternalcode.plots.feature.limit;

import com.eternalcode.plots.configuration.implementation.PluginConfiguration;
import com.eternalcode.plots.plot.old.PlotManager;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import org.bukkit.entity.Player;

import java.util.Map;

public class PlotsLimit {

    private final UserManager userManager;
    private final PlotManager plotManager;
    private final PluginConfiguration config;

    public PlotsLimit(UserManager userManager, PlotManager plotManager, PluginConfiguration config) {
        this.userManager = userManager;
        this.plotManager = plotManager;
        this.config = config;
    }

    public boolean hasLimit(Player player) {

        User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

        int limit = getLimit(player);
        int plots = 0;

        for (Plot plot : this.plotManager.getPlots(user)) {
            if (plot.getOwner().getUser().getUuid().equals(user.getUuid())) {
                plots++;
            }
        }

        return plots >= limit;
    }

    public int getLimit(Player player) {

        if (!config.plot.limits.containsKey("default")) {
            throw new NullPointerException("default limit for plots in config.yml is not set!");
        }

        int playerLimit = config.plot.limits.get("default");

        for (Map.Entry<String, Integer> entry : config.plot.limits.entrySet()) {

            String groupName = entry.getKey();
            int plotLimit = entry.getValue();

            if (groupName.equalsIgnoreCase("default")) {
                continue;
            }

            if (player.hasPermission("eternalplots.limit." + groupName)) {

                if (playerLimit < plotLimit) {
                    playerLimit = plotLimit;
                }
            }
        }

        return playerLimit;
    }
}
