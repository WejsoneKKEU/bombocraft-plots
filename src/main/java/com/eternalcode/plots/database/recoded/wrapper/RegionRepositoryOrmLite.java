package com.eternalcode.plots.database.recoded.wrapper;

import com.eternalcode.plots.database.recoded.persister.LocationPersister;
import com.eternalcode.plots.database.recoded.DatabaseManager;
import com.eternalcode.plots.plot.region.Region;
import com.eternalcode.plots.plot.region.RegionRepository;
import com.eternalcode.plots.position.PositionAdapter;
import com.eternalcode.plots.scheduler.Scheduler;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.UUID;

public class RegionRepositoryOrmLite extends AbstractRepositoryOrmLite implements RegionRepository {

    private RegionRepositoryOrmLite(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
    }

    public static RegionRepositoryOrmLite create(DatabaseManager databaseManager, Scheduler scheduler) {
        try {
            TableUtils.createTableIfNotExists(databaseManager.connectionSource(), RegionRepositoryOrmLite.RegionWrapper.class);
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return new RegionRepositoryOrmLite(databaseManager, scheduler);
    }

    @Override
    public void saveRegion(Region region) {
        this.save(RegionWrapper.class, RegionWrapper.from(region));
    }

    @DatabaseTable(tableName = "eternal_plots_regions")
    public static class RegionWrapper {
        @DatabaseField(columnName = "region_uuid", id = true)
        private UUID uuid;
        @DatabaseField(columnName = "region_size")
        private int size;
        @DatabaseField(columnName = "extend_level")
        private int extendLevel;
        @DatabaseField(columnName = "region_center", persisterClass = LocationPersister.class)
        private Location center;
        @DatabaseField(columnName = "region_max", persisterClass = LocationPersister.class)
        private Location posMax;
        @DatabaseField(columnName = "region_min", persisterClass = LocationPersister.class)
        private Location posMin;

        private RegionWrapper() {
        }

        private RegionWrapper(UUID regionUUID, int size, int extendLevel, Location posMax, Location posMin, Location center) {
            this.uuid = regionUUID;
            this.size = size;
            this.extendLevel = extendLevel;
            this.posMax = posMax;
            this.posMin = posMin;
            this.center = center;
        }

        public static RegionWrapper from(Region region) {
            return new RegionWrapper(region.getRegionUUID(), region.getSize(), region.getExtendLevel(), PositionAdapter.convert(region.getPosMax()), PositionAdapter.convert(region.getPosMin()), PositionAdapter.convert(region.getCenter()));
        }

        @java.lang.SuppressWarnings("all")
        public UUID getUuid() {
            return this.uuid;
        }

        @java.lang.SuppressWarnings("all")
        public int getSize() {
            return this.size;
        }

        @java.lang.SuppressWarnings("all")
        public int getExtendLevel() {
            return this.extendLevel;
        }

        @java.lang.SuppressWarnings("all")
        public Location getCenter() {
            return this.center;
        }

        @java.lang.SuppressWarnings("all")
        public Location getPosMax() {
            return this.posMax;
        }

        @java.lang.SuppressWarnings("all")
        public Location getPosMin() {
            return this.posMin;
        }
    }
}

