package com.eternalcode.plots.database.recoded.wrapper;

import com.eternalcode.plots.database.recoded.DatabaseManager;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.member.PlotMember;
import com.eternalcode.plots.plot.member.PlotMembersRepository;
import com.eternalcode.plots.scheduler.Scheduler;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class PlotMemberRepositoryOrmLite extends AbstractRepositoryOrmLite implements PlotMembersRepository {

    private PlotMemberRepositoryOrmLite(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
    }

    public static PlotMemberRepositoryOrmLite create(DatabaseManager databaseManager, Scheduler scheduler) {
        try {
            TableUtils.createTableIfNotExists(databaseManager.connectionSource(), PlotMemberRepositoryOrmLite.MemberWrapper.class);
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return new PlotMemberRepositoryOrmLite(databaseManager, scheduler);
    }

    @Override
    public void saveMembers(Plot plot, Set<PlotMember> plotMembers) {
        for (PlotMember plotMember : plotMembers) {
            this.save(MemberWrapper.class, MemberWrapper.from(PlotRepositoryOrmLite.PlotWrapper.from(plot), plotMember));
        }
    }

    @Override
    public void addMember(Plot plot, PlotMember plotMember) {
        this.saveIfNotExist(PlotRepositoryOrmLite.PlotWrapper.class, PlotRepositoryOrmLite.PlotWrapper.from(plot));
        this.save(MemberWrapper.class, MemberWrapper.from(PlotRepositoryOrmLite.PlotWrapper.from(plot), plotMember));
    }

    @Override
    public void removeMember(Plot plot, PlotMember plotMember) {
        this.saveIfNotExist(PlotRepositoryOrmLite.PlotWrapper.class, PlotRepositoryOrmLite.PlotWrapper.from(plot));
        this.delete(MemberWrapper.class, MemberWrapper.from(PlotRepositoryOrmLite.PlotWrapper.from(plot), plotMember));
    }

    @DatabaseTable(tableName = "eternal_plots_members")
    public static class MemberWrapper {
        @DatabaseField(columnName = "member_uuid", id = true)
        private UUID uuid;
        @DatabaseField(columnName = "user_uuid", foreign = true, foreignAutoRefresh = true)
        private UserRepositoryOrmLite.UserWrapper userWarp;
        @DatabaseField(columnName = "plot_uuid", foreign = true, foreignAutoRefresh = true)
        private PlotRepositoryOrmLite.PlotWrapper plotWarp;

        private MemberWrapper() {
        }

        private MemberWrapper(UUID uuid, UserRepositoryOrmLite.UserWrapper userWarp, PlotRepositoryOrmLite.PlotWrapper plotWarp) {
            this.uuid = uuid;
            this.userWarp = userWarp;
            this.plotWarp = plotWarp;
        }

        public static MemberWrapper from(PlotRepositoryOrmLite.PlotWrapper plotWarp, PlotMember plotMember) {
            return new MemberWrapper(plotMember.getUuid(), UserRepositoryOrmLite.UserWrapper.from(plotMember.getUser()), plotWarp);
        }

        @java.lang.SuppressWarnings("all")
        public UUID getUuid() {
            return this.uuid;
        }

        @java.lang.SuppressWarnings("all")
        public UserRepositoryOrmLite.UserWrapper getUserWarp() {
            return this.userWarp;
        }

        @java.lang.SuppressWarnings("all")
        public PlotRepositoryOrmLite.PlotWrapper getPlotWarp() {
            return this.plotWarp;
        }
    }
}
