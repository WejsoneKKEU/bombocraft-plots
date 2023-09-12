package com.eternalcode.plots.database.warp;

import com.eternalcode.plots.plot.Member;
import com.eternalcode.plots.user.User;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;

import java.util.UUID;

@DatabaseTable(tableName = "ep-users")
public class UserWarp {

    @Getter
    @DatabaseField(columnName = "user_uuid", id = true)
    private UUID UUID;

    @Getter
    @DatabaseField(columnName = "user_name")
    private String name;

    private UserWarp() {

    }

    private UserWarp(UUID UUID, String name) {
        this.UUID = UUID;
        this.name = name;
    }

    public static UserWarp from(User user) {
        return new UserWarp(user.getUuid(), user.getName());
    }

    public static UserWarp from(Member member) {
        return new UserWarp(member.getUser().getUuid(), member.getUser().getName());
    }
}
