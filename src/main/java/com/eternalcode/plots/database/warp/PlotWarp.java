package com.eternalcode.plots.database.warp;

import com.eternalcode.plots.plot.Plot;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "ep-plots")
public class PlotWarp {

    @Getter
    @DatabaseField(columnName = "plot_uuid", id = true)
    private UUID uuid;

    @Getter
    @DatabaseField(columnName = "plot_region", foreign = true, foreignAutoRefresh = true)
    private RegionWarp region;

    @Getter
    @DatabaseField(columnName = "plot_owner", foreign = true, foreignAutoRefresh = true)
    private MemberWarp owner;

    @Getter
    @DatabaseField(columnName = "plot_name")
    private String name;

    @Getter
    @DatabaseField(columnName = "plot_created")
    private Date created;

    @Getter
    @DatabaseField(columnName = "plot_expires")
    private Date expires;

    @Getter
    @ForeignCollectionField
    private ForeignCollection<FlagWarp> flags;

    @Getter
    @ForeignCollectionField
    private ForeignCollection<MemberWarp> members;

    private PlotWarp() {

    }

    private PlotWarp(UUID uuid, RegionWarp regionWarp, String name, Date created, Date expires) {
        this.uuid = uuid;
        this.region = regionWarp;
        this.name = name;
        this.created = created;
        this.expires = expires;
    }

    public static PlotWarp from(Plot plot) {
        PlotWarp plotWarp = new PlotWarp(
            plot.getUuid(),
            RegionWarp.from(plot.getRegion()),
            plot.getName(),
            plot.getCreated(),
            plot.getExpires()
        );
        plotWarp.owner = MemberWarp.from(plotWarp, plot.getOwner());
        return plotWarp;
    }

}
