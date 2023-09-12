package com.eternalcode.plots.plot;

import com.eternalcode.plots.configuration.implementations.ProtectionConfiguration;
import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.region.Region;
import com.eternalcode.plots.region.RegionManager;
import com.eternalcode.plots.user.User;
import org.bukkit.Bukkit;
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
    private final MembersRepository membersRepository;
    private final PlotRepository plotRepository;
    private final RegionManager regionManager;
    private final ProtectionConfiguration protectionConfiguration;

    public PlotManager(MembersRepository membersRepository, PlotRepository plotRepository, RegionManager regionManager, ProtectionConfiguration protectionConfiguration) {
        this.membersRepository = membersRepository;
        this.protectionConfiguration = protectionConfiguration;
        this.plotRepository = plotRepository;
        this.regionManager = regionManager;

        this.plotRepository.loadAllPlot().thenAccept(plotsFromRepo -> {
            for (Plot plot : plotsFromRepo) {
                this.plots.put(plot.getUuid(), plot);

                Bukkit.getLogger().info("Successfully loaded " + this.plots.size() + " plots");
            }
        });
    }

    public Option<Plot> create(UUID plotUUID, String name, User owner, Region region, Date creation, Date validity) {
        if (this.plots.containsKey(plotUUID)) {
            return Option.none();
        }

        Member memberOwner = new Member(UUID.randomUUID(), owner);

        Plot plot = new Plot(
            plotUUID,
            name,
            memberOwner,
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
        Member member = new Member(UUID.randomUUID(), user);

        plot.addMember(member);

        this.membersRepository.saveMembers(plot, plot.getMembers());
        //this.membersRepository.addMember(plot, member);
    }

    public void removeMember(Plot plot, User user) {
        Member member = plot.getMember(user).get();

        plot.removeMember(member);

        this.membersRepository.removeMember(plot, member);
        //this.membersRepository.removeMember(plot, member);
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

    public Option<Region> getRegion(Location location) {
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
