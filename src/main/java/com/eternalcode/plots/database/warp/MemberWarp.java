package com.eternalcode.plots.database.warp;

import com.eternalcode.plots.plot.Member;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;

@DatabaseTable(tableName = "ep-members")
public class MemberWarp {
    @DatabaseField(columnName = "member_uuid", id = true)
    private UUID uuid;
    @DatabaseField(columnName = "user_uuid", foreign = true, foreignAutoRefresh = true)
    private UserWarp userWarp;
    @DatabaseField(columnName = "plot_uuid", foreign = true, foreignAutoRefresh = true)
    private PlotWarp plotWarp;

    private MemberWarp() {
    }

    private MemberWarp(UUID uuid, UserWarp userWarp, PlotWarp plotWarp) {
        this.uuid = uuid;
        this.userWarp = userWarp;
        this.plotWarp = plotWarp;
    }

    public static MemberWarp from(PlotWarp plotWarp, Member member) {
        return new MemberWarp(member.getUuid(), UserWarp.from(member.getUser()), plotWarp);
    }

    @java.lang.SuppressWarnings("all")
    public UUID getUuid() {
        return this.uuid;
    }

    @java.lang.SuppressWarnings("all")
    public UserWarp getUserWarp() {
        return this.userWarp;
    }

    @java.lang.SuppressWarnings("all")
    public PlotWarp getPlotWarp() {
        return this.plotWarp;
    }
}
