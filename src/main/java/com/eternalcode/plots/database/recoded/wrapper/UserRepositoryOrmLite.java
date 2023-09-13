package com.eternalcode.plots.database.recoded.wrapper;

import com.eternalcode.plots.database.recoded.DatabaseManager;
import com.eternalcode.plots.plot.member.PlotMember;
import com.eternalcode.plots.scheduler.Scheduler;
import com.eternalcode.plots.user.User;
import com.eternalcode.plots.user.UserRepository;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import panda.std.reactive.Completable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserRepositoryOrmLite extends AbstractRepositoryOrmLite implements UserRepository {

    private UserRepositoryOrmLite(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
    }

    public static UserRepositoryOrmLite create(DatabaseManager databaseManager, Scheduler scheduler) {
        try {
            TableUtils.createTableIfNotExists(databaseManager.connectionSource(), UserRepositoryOrmLite.UserWrapper.class);
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        return new UserRepositoryOrmLite(databaseManager, scheduler);
    }

    @Override
    public Completable<List<User>> getUsers() {
        return this.selectAll(UserWrapper.class)
            .thenApply(userWarps -> userWarps.stream().map(this::adaptUser).toList());
    }

    @Override
    public CompletableFuture<User> getUser(UUID uuid) {
        return this.select(UserWrapper.class, uuid)
            .thenApply(this::adaptUser).toFuture();
    }

    @Override
    public void saveUser(User user) {
        this.save(UserWrapper.class, UserWrapper.from(user));
    }

    private User adaptUser(UserWrapper userWarp) {
        return new User(userWarp.getUuid(), userWarp.getName());
    }

    @DatabaseTable(tableName = "eternal_plots_users")
    public static class UserWrapper {
        @DatabaseField(columnName = "user_uuid", id = true)
        private UUID uuid;
        @DatabaseField(columnName = "user_name")
        private String name;

        private UserWrapper() {
        }

        private UserWrapper(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public static UserWrapper from(User user) {
            return new UserWrapper(user.getUuid(), user.getName());
        }

        public static UserWrapper from(PlotMember plotMember) {
            return new UserWrapper(plotMember.getUser().getUuid(), plotMember.getUser().getName());
        }

        @java.lang.SuppressWarnings("all")
        public UUID getUuid() {
            return this.uuid;
        }

        @java.lang.SuppressWarnings("all")
        public String getName() {
            return this.name;
        }
    }

}
