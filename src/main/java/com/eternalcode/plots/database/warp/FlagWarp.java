package com.eternalcode.plots.database.warp;

import com.eternalcode.plots.plot.protection.Flag;
import com.eternalcode.plots.plot.protection.FlagType;
import com.eternalcode.plots.plot.protection.Protection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@DatabaseTable(tableName = "ep-flags")
public class FlagWarp {

    @Getter
    @DatabaseField(columnName = "flag_uuid", id = true)
    private UUID uuid;

    @Getter
    @DatabaseField(columnName = "plot_uuid", foreign = true, foreignAutoRefresh = true)
    private PlotWarp plotWarp;

    @Getter
    @DatabaseField(columnName = "protection_type")
    private FlagType flagType;

    @Getter
    @DatabaseField(columnName = "status")
    private boolean status;

    private FlagWarp() {

    }

    private FlagWarp(UUID uuid, FlagType flagType, PlotWarp plotWarp, boolean status) {
        this.uuid = uuid;
        this.flagType = flagType;
        this.plotWarp = plotWarp;
        this.status = status;
    }

    public static Set<FlagWarp> from(PlotWarp plotWarp, Protection protection) {

        Set<FlagWarp> flagWarps = new HashSet<>();

        for (Flag flag : protection.getFlags()) {

            if (protection.getFlagState(flag.getFlagType()).isEmpty()) {
                continue;
            }

            FlagWarp flagWarp = new FlagWarp(
                flag.getUuid(),
                flag.getFlagType(),
                plotWarp,
                protection.getFlagState(flag.getFlagType()).get());

            flagWarps.add(flagWarp);
        }

        return flagWarps;
    }

    public static FlagWarp from(PlotWarp plotWarp, Flag flag) {
        return new FlagWarp(flag.getUuid(), flag.getFlagType(), plotWarp, flag.getStatus());
    }
}
