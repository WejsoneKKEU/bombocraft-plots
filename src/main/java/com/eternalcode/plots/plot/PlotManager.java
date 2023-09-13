package com.eternalcode.plots.plot;

import com.eternalcode.plots.configuration.implementation.ProtectionConfiguration;
import com.eternalcode.plots.plot.member.PlotMember;
import com.eternalcode.plots.plot.member.PlotMembersRepository;
import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.plot.region.Region;
import com.eternalcode.plots.plot.region.RegionManager;
import com.eternalcode.plots.user.User;
import org.bukkit.Location;
import panda.std.Option;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlotManager {

    private final Map<UUID, Plot> plots = new ConcurrentHashMap<>();
    private final PlotMembersRepository plotMembersRepository;
    private final PlotRepository plotRepository;
    private final RegionManager regionManager;
    private final ProtectionConfiguration protectionConfiguration;

    public PlotManager(PlotMembersRepository plotMembersRepository, PlotRepository plotRepository, RegionManager regionManager, ProtectionConfiguration protectionConfiguration) {
        this.plotMembersRepository = plotMembersRepository;
        this.protectionConfiguration = protectionConfiguration;
        this.plotRepository = plotRepository;
        this.regionManager = regionManager;

        this.plotRepository.loadAllPlot().thenAccept(plotsFromRepo -> {
            for (Plot plot : plotsFromRepo) {
                this.plots.put(plot.getUuid(), plot);
            }
        });
    }

    public Option<Plot> create(UUID plotUUID, String name, User owner, Region region, Date creation, Date validity) {
        if (this.plots.containsKey(plotUUID)) {
            return Option.none();
        }

        PlotMember plotMemberOwner = new PlotMember(UUID.randomUUID(), owner);

        Plot plot = new Plot(
            plotUUID,
            name,
            plotMemberOwner,
            region,
            creation,
            validity,
            new HashSet<>()
        );

        Protection protection = new Protection(this.protectionConfiguration);
        plot.setProtection(protection);

        this.plots.put(plotUUID, plot);
        this.plotRepository.savePlot(plot);

        return Option.of(plot);
    }

    public void delete(Plot plot) {
        this.plots.remove(plot.getUuid());
        this.plotRepository.deletePlot(plot);
    }

    public void addMember(Plot plot, User user) {
        PlotMember plotMember = new PlotMember(UUID.randomUUID(), user);

        plot.addMember(plotMember);

        this.plotMembersRepository.saveMembers(plot, plot.getMembers());
    }

    public void removeMember(Plot plot, User user) {
        PlotMember plotMember = plot.getMember(user).get();

        plot.removeMember(plotMember);

        this.plotMembersRepository.removeMember(plot, plotMember);
    }

    public void setName(Plot plot, String name) {
        plot.setName(name);

        this.plotRepository.savePlot(plot);
    }

    public void setExpires(Plot plot, Date expires) {
        plot.setExpires(expires);

        this.plotRepository.savePlot(plot);
    }

    public void setSize(Plot plot, int size) {
        this.regionManager.setSize(plot.getRegion(), size);

        this.plotRepository.savePlot(plot);
    }

    public void setExtendLevel(Plot plot, int level) {
        this.regionManager.setExtendLevel(plot.getRegion(), level);

        this.plotRepository.savePlot(plot);
    }

    public boolean isNameBusy(String name) {
        for (Plot plot : this.plots.values()) {
            if (plot.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public Option<Plot> getPlot(UUID plotUUID) {
        return Option.of(this.plots.get(plotUUID));
    }

    public Option<Plot> getPlot(String plotName) {
        for (Plot plot : this.plots.values()) {

            if (plot.getName().equalsIgnoreCase(plotName)) {
                return Option.of(plot);
            }
        }
        return Option.none();
    }

    public Plot getPlot(Region region) {
        for (Plot plot : this.plots.values()) {

            if (plot.getRegion() == region) {
                return plot;
            }
        }

        throw new NullPointerException("Region (UUID: " + region.getRegionUUID() + ") exists without plot!");
    }

    public Option<Plot> getPlot(PlotManager plotManager, User user, Option<Plot> plotOpt) {
        if (plotOpt.isEmpty()) {

            Set<Plot> userPlots = plotManager.getPlots(user);

            if (userPlots.size() != 1) {
                return Option.none();
            }

            return Option.of(userPlots.iterator().next());

        }

        return Option.of(plotOpt.get());
    }

    public Option<Region> getPlotRegionByLocation(Location location) {
        double x = location.getX();
        double z = location.getZ();

        if (location.getWorld() == null) {
            throw new IllegalArgumentException("World in location is null");
        }

        String locationWorldName = location.getWorld().getName();

        for (Plot plot : this.plots.values()) {

            Region region = plot.getRegion();

            String worldName = region.getCenter().getWorld();
            double minX = region.getPosMin().getX();
            double minZ = region.getPosMin().getZ();
            double maxX = region.getPosMax().getX();
            double maxZ = region.getPosMax().getZ();

            if ((x <= maxX && x >= minX) && (z <= maxZ && z >= minZ) && worldName.equalsIgnoreCase(locationWorldName)) {
                return Option.of(region);
            }
        }

        return Option.none();
    }

    public Set<Plot> getPlots() {
        return new HashSet<>(this.plots.values());
    }

    public Set<Plot> getPlots(User user) {
        Set<Plot> plots = new HashSet<>();

        for (Plot plot : this.plots.values()) {
            if (plot.isMember(user)) {
                plots.add(plot);
            }
        }

        return plots;
    }

}
