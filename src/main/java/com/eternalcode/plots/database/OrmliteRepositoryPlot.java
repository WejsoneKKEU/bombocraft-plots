package com.eternalcode.plots.database;

import com.eternalcode.plots.database.warp.FlagWarp;
import com.eternalcode.plots.database.warp.MemberWarp;
import com.eternalcode.plots.database.warp.PlotWarp;
import com.eternalcode.plots.database.warp.RegionWarp;
import com.eternalcode.plots.database.warp.UserWarp;
import com.eternalcode.plots.plot.member.PlotMember;
import com.eternalcode.plots.plot.member.PlotMembersRepository;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.PlotRepository;
import com.eternalcode.plots.plot.protection.Flag;
import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.plot.protection.ProtectionRepository;
import com.eternalcode.plots.plot.region.Region;
import com.eternalcode.plots.plot.region.RegionRepository;
import com.eternalcode.plots.scheduler.Scheduler;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserRepository;
import com.j256.ormlite.dao.Dao;
import panda.std.function.ThrowingFunction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// TODO: Separate this class into multiple classes
public class OrmliteRepositoryPlot implements PlotRepository, UserRepository, RegionRepository, PlotMembersRepository, ProtectionRepository {

    private final DatabaseManager manager;
    private final Scheduler scheduler;

    public OrmliteRepositoryPlot(DatabaseManager manager, Scheduler scheduler) {
        this.manager = manager;
        this.scheduler = scheduler;
    }

    /**
     * Getters From DB
     **/

    @Override
    public CompletableFuture<Plot> loadPlot(UUID plotUUID) {
        return this.select(PlotWarp.class, plotUUID)
            .thenApply(this::adaptPlot);
    }

    @Override
    public CompletableFuture<List<Plot>> loadAllPlot() {
        return this.selectAll(PlotWarp.class)
            .thenApply(plotWarps -> plotWarps.stream().map(this::adaptPlot).toList());
    }

    @Override
    public CompletableFuture<List<User>> getUsers() {
        return this.selectAll(UserWarp.class)
            .thenApply(userWarps -> userWarps.stream().map(this::adaptUser).toList());
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        return this.select(UserWarp.class, uuid)
            .thenApply(this::adaptUser);
    }

    /**
     * Setters To DB
     **/

    @Override
    public void savePlot(Plot plot) {
        this.saveRegion(plot.getRegion());
        this.save(PlotWarp.class, PlotWarp.from(plot));
        this.saveProtection(plot, plot.getProtection());
        this.saveMembers(plot, plot.getMembers());
    }

    @Override
    public void deletePlot(Plot plot) {
        PlotWarp plotWarp = PlotWarp.from(plot);
        RegionWarp regionWarp = RegionWarp.from(plot.getRegion());
        List<MemberWarp> memberWarps = new ArrayList<>();
        List<FlagWarp> flagWarps = new ArrayList<>();

        for (PlotMember plotMember : plot.getMembers()) {
            memberWarps.add(MemberWarp.from(plotWarp, plotMember));
        }

        for (Flag flag : plot.getProtection().getFlags()) {
            flagWarps.add(FlagWarp.from(plotWarp, flag));
        }

        remove(PlotWarp.class, plotWarp);
        remove(RegionWarp.class, regionWarp);
        remove(MemberWarp.class, memberWarps);
        remove(FlagWarp.class, flagWarps);
    }

    @Override
    public void saveRegion(Region region) {
        this.save(RegionWarp.class, RegionWarp.from(region));
    }

    @Override
    public void saveUser(User user) {
        this.save(UserWarp.class, UserWarp.from(user));
    }

    @Override
    public void saveMembers(Plot plot, Set<PlotMember> plotMembers) {
        //this.saveIfNotExist(PlotWarp.class, PlotWarp.from(plot));

        for (PlotMember plotMember : plotMembers) {
            this.save(MemberWarp.class, MemberWarp.from(PlotWarp.from(plot), plotMember));
        }
    }

    @Override
    public void addMember(Plot plot, PlotMember plotMember) {
        this.saveIfNotExist(PlotWarp.class, PlotWarp.from(plot));
        this.save(MemberWarp.class, MemberWarp.from(PlotWarp.from(plot), plotMember));
    }

    @Override
    public void removeMember(Plot plot, PlotMember plotMember) {
        this.saveIfNotExist(PlotWarp.class, PlotWarp.from(plot));
        this.remove(MemberWarp.class, MemberWarp.from(PlotWarp.from(plot), plotMember));
    }

    @Override
    public void saveProtection(Plot plot, Protection protection) {
        this.saveIfNotExist(PlotWarp.class, PlotWarp.from(plot));

        for (FlagWarp flagWarp : FlagWarp.from(PlotWarp.from(plot), protection)) {
            this.save(FlagWarp.class, flagWarp);
        }
    }

    /**
     * DB Functions
     **/

    private <T> CompletableFuture<Dao.CreateOrUpdateStatus> save(Class<T> type, T warp) {
        return this.action(type, dao -> dao.createOrUpdate(warp));
    }

    private <T> CompletableFuture<T> saveIfNotExist(Class<T> type, T warp) {
        return this.action(type, dao -> dao.createIfNotExists(warp));
    }

    private <T, ID> CompletableFuture<T> select(Class<T> type, ID id) {
        return this.action(type, dao -> dao.queryForId(id));
    }

    private <T> CompletableFuture<Integer> remove(Class<T> type, T warp) {
        return this.action(type, dao -> dao.delete(warp));
    }

    private <T> CompletableFuture<Integer> remove(Class<T> type, Collection<T> warp) {
        return this.action(type, dao -> dao.delete(warp));
    }

    private <T> CompletableFuture<List<T>> selectAll(Class<T> type) {
        return this.action(type, Dao::queryForAll);
    }

    private <T, ID, R> CompletableFuture<R> action(Class<T> type, ThrowingFunction<Dao<T, ID>, R, SQLException> action) {
        CompletableFuture<R> completableFuture = new CompletableFuture<>();

        this.scheduler.runTaskAsynchronously(() -> {
            Dao<T, ID> dao = this.manager.getDao(type);

            try {
                completableFuture.complete(action.apply(dao));
            }
            catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });

        return completableFuture;
    }

    /**
     * Adapt Methods
     **/

    private Plot adaptPlot(PlotWarp plotWarp) {
        PlotMember owner = adaptMember(plotWarp.getOwner());
        Region region = adaptRegion(plotWarp.getRegion());
        Protection protection = adaptProtection(plotWarp);
        Set<PlotMember> plotMembers = adaptMembers(plotWarp);

        Plot plot = new Plot(
            plotWarp.getUuid(),
            plotWarp.getName(),
            owner,
            region,
            plotWarp.getCreated(),
            plotWarp.getExpires(),
            plotMembers
        );

        plot.setProtection(protection);

        return plot;
    }

    private PlotMember adaptMember(MemberWarp memberWarp) {
        User user = adaptUser(memberWarp.getUserWarp());
        return new PlotMember(memberWarp.getUuid(), user);
    }

    private Region adaptRegion(RegionWarp regionWarp) {
        return new Region(regionWarp.getUuid(), regionWarp.getSize(), regionWarp.getExtendLevel(), regionWarp.getPosMax(), regionWarp.getPosMin(), regionWarp.getCenter());
    }

    private User adaptUser(UserWarp userWarp) {
        return new User(userWarp.getUUID(), userWarp.getName());
    }

    private Protection adaptProtection(PlotWarp plotWarp) {
        Set<Flag> flags = adaptFlags(plotWarp);
        return new Protection(flags);
    }

    private Set<Flag> adaptFlags(PlotWarp plotWarp) {
        Set<Flag> flags = new HashSet<>();

        for (FlagWarp flagWarp : plotWarp.getFlags()) {
            flags.add(new Flag(flagWarp.getUuid(), flagWarp.getFlagType(), flagWarp.isStatus()));
        }

        return flags;
    }

    private Set<PlotMember> adaptMembers(PlotWarp plotWarp) {
        Set<PlotMember> hashSet = new HashSet<>();

        for (MemberWarp member : plotWarp.getMembers()) {
            hashSet.add(this.adaptMember(member));
        }

        return hashSet;
    }
}
