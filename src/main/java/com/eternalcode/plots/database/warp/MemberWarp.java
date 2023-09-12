package com.eternalcode.plots.database.warp;

import com.eternalcode.plots.plot.Member;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "ep-members")
public class MemberWarp {

    @Getter
    @DatabaseField(columnName = "member_uuid", id = true)
    private UUID uuid;

    @Getter
    @DatabaseField(columnName = "user_uuid", foreign = true, foreignAutoRefresh = true)
    private UserWarp userWarp;

    @Getter
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
}
