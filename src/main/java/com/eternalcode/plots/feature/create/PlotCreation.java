package com.eternalcode.plots.feature.create;

import com.eternalcode.plots.adventure.LegacyUtils;
import com.eternalcode.plots.configuration.implementation.LanguageConfiguration;
import com.eternalcode.plots.configuration.implementation.PluginConfiguration;
import com.eternalcode.plots.feature.name.PlotChangeName;
import com.eternalcode.plots.notification.NotificationAnnouncer;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotManager;
import com.eternalcode.plots.plot.region.Region;
import com.eternalcode.plots.plot.region.RegionManager;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import panda.std.Option;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class PlotCreation {

    private final PluginConfiguration pluginConfiguration;
    private final LanguageConfiguration lang;
    private final UserManager userManager;
    private final PlotManager plotManager;
    private final RegionManager regionManager;
    private final Plugin plugin;
    private final NotificationAnnouncer notificationAnnouncer;

    public PlotCreation(PluginConfiguration pluginConfiguration, LanguageConfiguration languageConfiguration, UserManager userManager, PlotManager plotManager, RegionManager regionManager, Plugin plugin, NotificationAnnouncer notificationAnnouncer) {
        this.pluginConfiguration = pluginConfiguration;
        this.lang = languageConfiguration;
        this.userManager = userManager;
        this.plotManager = plotManager;
        this.regionManager = regionManager;
        this.plugin = plugin;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    public void createPlot(Player player, Location center, int startSize) {

        // get user
        User user = this.userManager.getOrCreate(player.getUniqueId(), player.getName());

        // check if generated uuid exists
        UUID plotUUID;
        do {
            plotUUID = UUID.randomUUID();
        }
        while (this.plotManager.getPlot(plotUUID).isPresent());

        // get plot default name (player_name{nr})
        int nr = 0;
        do {
            nr++;
        }
        while (this.plotManager.getPlot(player.getName() + nr).isPresent());
        String plotName = player.getName() + nr;

        // fix start size (for one addon block in center)
        if (startSize % 2 == 0) {
            startSize++;
        }

        // get plot size and range
        int range = startSize / 2;

        // setup min, max plot locations
        Location min = new Location(center.getWorld(), center.getX() - range, center.getY(), center.getZ() - range);
        Location max = new Location(center.getWorld(), center.getX() + range, center.getY(), center.getZ() + range);

        // setup region
        Region region = this.regionManager.create(startSize, max, min, center);

        // setup expires date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, this.pluginConfiguration.validityDays);
        Date validity = calendar.getTime();

        // create plot
        Option<Plot> plotOpt = this.plotManager.create(
            plotUUID,
            plotName,
            user,
            region,
            new Date(),
            validity
        );

        if (plotOpt.isEmpty()) {
            throw new IllegalStateException("Plot cannot be created: Plot with this UUID already exists");
        }

        center.getBlock().setType(Material.CAMPFIRE);

        Plot plot = plotOpt.get();

        // create anvil (name change)
        new PlotChangeName(plot, this.lang, this.plotManager, this.plugin, this.notificationAnnouncer).sendGui(player);

        player.sendMessage(LegacyUtils.color(this.lang.plotCreation.plotCreated.replace("{NAME}", plot.getName())));
    }
}
