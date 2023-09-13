package com.eternalcode.plots.database.recoded.wrapper;

import com.eternalcode.plots.database.recoded.DatabaseManager;
import com.eternalcode.plots.plot.Plot;
import com.eternalcode.plots.plot.protection.Flag;
import com.eternalcode.plots.plot.protection.FlagType;
import com.eternalcode.plots.plot.protection.Protection;
import com.eternalcode.plots.plot.protection.ProtectionRepository;
import com.eternalcode.plots.scheduler.Scheduler;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlotFlagRepositoryOrmLite extends AbstractRepositoryOrmLite implements ProtectionRepository {

    private PlotFlagRepositoryOrmLite(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
    }

    public static PlotFlagRepositoryOrmLite create(DatabaseManager databaseManager, Scheduler scheduler) {
        try {
            TableUtils.createTableIfNotExists(databaseManager.connectionSource(), PlotFlagWrapper.class);
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return new PlotFlagRepositoryOrmLite(databaseManager, scheduler);
    }

    @Override
    public void saveProtection(Plot plot, Protection protection) {
        this.saveIfNotExist(PlotRepositoryOrmLite.PlotWrapper.class, PlotRepositoryOrmLite.PlotWrapper.from(plot));

        for (PlotFlagWrapper plotFlagWrapper : PlotFlagWrapper.from(PlotRepositoryOrmLite.PlotWrapper.from(plot), protection)) {
            this.save(PlotFlagWrapper.class, plotFlagWrapper);
        }
    }

    @DatabaseTable(tableName = "eternal_plots_flags")
    public static class PlotFlagWrapper {
        @DatabaseField(columnName = "flag_uuid", id = true)
        private UUID uuid;
        @DatabaseField(columnName = "plot_uuid", foreign = true, foreignAutoRefresh = true)
        private PlotRepositoryOrmLite.PlotWrapper plotWarp;
        @DatabaseField(columnName = "protection_type")
        private FlagType flagType;
        @DatabaseField(columnName = "status")
        private boolean status;

        private PlotFlagWrapper() {
        }

        private PlotFlagWrapper(UUID uuid, FlagType flagType, PlotRepositoryOrmLite.PlotWrapper plotWarp, boolean status) {
            this.uuid = uuid;
            this.flagType = flagType;
            this.plotWarp = plotWarp;
            this.status = status;
        }

        public static Set<PlotFlagWrapper> from(PlotRepositoryOrmLite.PlotWrapper plotWarp, Protection protection) {
            Set<PlotFlagWrapper> plotFlagWrappers = new HashSet<>();
            for (Flag flag : protection.getFlags()) {
                if (protection.getFlagState(flag.getFlagType()).isEmpty()) {
                    continue;
                }
                PlotFlagWrapper plotFlagWrapper = new PlotFlagWrapper(flag.getUuid(), flag.getFlagType(), plotWarp, protection.getFlagState(flag.getFlagType()).get());
                plotFlagWrappers.add(plotFlagWrapper);
            }
            return plotFlagWrappers;
        }

        public static PlotFlagWrapper from(PlotRepositoryOrmLite.PlotWrapper plotWarp, Flag flag) {
            return new PlotFlagWrapper(flag.getUuid(), flag.getFlagType(), plotWarp, flag.getStatus());
        }

        @java.lang.SuppressWarnings("all")
        public UUID getUuid() {
            return this.uuid;
        }

        @java.lang.SuppressWarnings("all")
        public PlotRepositoryOrmLite.PlotWrapper getPlotWarp() {
            return this.plotWarp;
        }

        @java.lang.SuppressWarnings("all")
        public FlagType getFlagType() {
            return this.flagType;
        }

        @java.lang.SuppressWarnings("all")
        public boolean isStatus() {
            return this.status;
        }
    }

}
