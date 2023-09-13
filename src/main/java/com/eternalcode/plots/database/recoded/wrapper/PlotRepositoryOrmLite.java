package com.eternalcode.plots.database.recoded.wrapper;

import com.eternalcode.plots.database.recoded.DatabaseManager;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotRepository;
import com.eternalcode.plots.plot.member.PlotMember;
import com.eternalcode.plots.plot.protection.Flag;
import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.plot.region.Region;
import com.eternalcode.plots.scheduler.Scheduler;
import com.eternalcode.plots.user.User;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import panda.std.reactive.Completable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlotRepositoryOrmLite extends AbstractRepositoryOrmLite implements PlotRepository {

    private PlotRepositoryOrmLite(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
    }

    @Override
    public CompletableFuture<Plot> loadPlot(UUID plotUUID) {
        return this.select(PlotWrapper.class, plotUUID)
            .thenApply(this::adaptPlot).toFuture();
    }

    @Override
    public Completable<List<Plot>> loadAllPlot() {
        return this.selectAll(PlotWrapper.class)
            .thenApply(plotWarps -> plotWarps.stream().map(this::adaptPlot).toList());
    }

    @Override
    public void savePlot(Plot plot) {
        this.save(PlotWrapper.class, PlotWrapper.from(plot));

        this.saveRegion(plot.getRegion());
        this.saveProtection(plot, plot.getProtection());
        this.saveMembers(plot, plot.getMembers());
    }


    @Override
    public void deletePlot(Plot plot) {
        PlotWrapper plotWarp = PlotWrapper.from(plot);
        RegionRepositoryOrmLite.RegionWrapper regionWarp = RegionRepositoryOrmLite.RegionWrapper.from(plot.getRegion());
        List<PlotMemberRepositoryOrmLite.MemberWrapper> memberWarps = new ArrayList<>();
        List<PlotFlagRepositoryOrmLite.PlotFlagWrapper> flagWarps = new ArrayList<>();

        for (PlotMember member : plot.getMembers()) {
            memberWarps.add(PlotMemberRepositoryOrmLite.MemberWrapper.from(plotWarp, member));
        }

        for (Flag flag : plot.getProtection().getFlags()) {
            flagWarps.add(PlotFlagRepositoryOrmLite.PlotFlagWrapper.from(plotWarp, flag));
        }

        delete(PlotWrapper.class, plotWarp);
        delete(RegionRepositoryOrmLite.RegionWrapper.class, regionWarp);
        delete(PlotMemberRepositoryOrmLite.MemberWrapper.class, memberWarps);
        delete(PlotFlagRepositoryOrmLite.PlotFlagWrapper.class, flagWarps);
    }


    private Plot adaptPlot(PlotWrapper plotWrapper) {
        PlotMember owner = adaptMember(plotWrapper.getOwner());
        Region region = adaptRegion(plotWrapper.getRegion());
        Protection protection = adaptProtection(plotWrapper);
        Set<PlotMember> members = adaptMembers(plotWrapper);

        Plot plot = new Plot(
            plotWrapper.getUuid(),
            plotWrapper.getName(),
            owner,
            region,
            plotWrapper.getCreated(),
            plotWrapper.getExpires(),
            members
        );

        plot.setProtection(protection);

        return plot;
    }

    private Set<PlotMember> adaptMembers(PlotWrapper plotWrapper) {
        Set<PlotMember> hashSet = new HashSet<>();

        for (PlotMemberRepositoryOrmLite.MemberWrapper member : plotWrapper.getMembers()) {
            hashSet.add(this.adaptMember(member));
        }

        return hashSet;
    }

    private PlotMember adaptMember(PlotMemberRepositoryOrmLite.MemberWrapper memberWrapper) {
        User user = adaptUser(memberWrapper.getUserWarp());
        return new PlotMember(memberWrapper.getUuid(), user);
    }

    private Region adaptRegion(RegionRepositoryOrmLite.RegionWrapper regionWrapper) {
        return new Region(regionWrapper.getUuid(), regionWrapper.getSize(), regionWrapper.getExtendLevel(), regionWrapper.getPosMax(), regionWrapper.getPosMin(), regionWrapper.getCenter());
    }

    private User adaptUser(UserRepositoryOrmLite.UserWrapper userWrapper) {
        return new User(userWrapper.getUuid(), userWrapper.getName());
    }

    private Protection adaptProtection(PlotWrapper plotWrapper) {
        Set<Flag> flags = adaptFlags(plotWrapper);
        return new Protection(flags);
    }

    private Set<Flag> adaptFlags(PlotWrapper plotWrapper) {
        Set<Flag> flags = new HashSet<>();

        for (PlotFlagRepositoryOrmLite.PlotFlagWrapper flagWrapper : plotWrapper.getFlags()) {
            flags.add(new Flag(flagWrapper.getUuid(), flagWrapper.getFlagType(), flagWrapper.isStatus()));
        }

        return flags;
    }

    @DatabaseTable(tableName = "eternal_plots_plots")
    public static class PlotWrapper {
        @DatabaseField(columnName = "plot_uuid", id = true)
        private UUID uuid;
        @DatabaseField(columnName = "plot_region", foreign = true, foreignAutoRefresh = true)
        private RegionRepositoryOrmLite.RegionWrapper region;
        @DatabaseField(columnName = "plot_owner", foreign = true, foreignAutoRefresh = true)
        private PlotMemberRepositoryOrmLite.MemberWrapper owner;
        @DatabaseField(columnName = "plot_name")
        private String name;
        @DatabaseField(columnName = "plot_created")
        private Date created;
        @DatabaseField(columnName = "plot_expires")
        private Date expires;
        @ForeignCollectionField
        private ForeignCollection<PlotFlagRepositoryOrmLite.PlotFlagWrapper> flags;
        @ForeignCollectionField
        private ForeignCollection<PlotMemberRepositoryOrmLite.MemberWrapper> members;

        private PlotWrapper() {
        }

        private PlotWrapper(UUID uuid, RegionRepositoryOrmLite.RegionWrapper regionWarp, String name, Date created, Date expires) {
            this.uuid = uuid;
            this.region = regionWarp;
            this.name = name;
            this.created = created;
            this.expires = expires;
        }

        public static PlotWrapper from(Plot plot) {
            PlotWrapper plotWrapper = new PlotWrapper(plot.getUuid(), RegionRepositoryOrmLite.RegionWrapper.from(plot.getRegion()), plot.getName(), plot.getCreated(), plot.getExpires());
            plotWrapper.owner = PlotMemberRepositoryOrmLite.MemberWrapper.from(PlotWrapper.from(plot), plot.getOwner());
            return plotWrapper;
        }

        @java.lang.SuppressWarnings("all")
        public UUID getUuid() {
            return this.uuid;
        }

        @java.lang.SuppressWarnings("all")
        public RegionRepositoryOrmLite.RegionWrapper getRegion() {
            return this.region;
        }

        @java.lang.SuppressWarnings("all")
        public PlotMemberRepositoryOrmLite.MemberWrapper getOwner() {
            return this.owner;
        }

        @java.lang.SuppressWarnings("all")
        public String getName() {
            return this.name;
        }

        @java.lang.SuppressWarnings("all")
        public Date getCreated() {
            return this.created;
        }

        @java.lang.SuppressWarnings("all")
        public Date getExpires() {
            return this.expires;
        }

        @java.lang.SuppressWarnings("all")
        public ForeignCollection<PlotFlagRepositoryOrmLite.PlotFlagWrapper> getFlags() {
            return this.flags;
        }

        @java.lang.SuppressWarnings("all")
        public ForeignCollection<PlotMemberRepositoryOrmLite.MemberWrapper> getMembers() {
            return this.members;
        }
    }

}
