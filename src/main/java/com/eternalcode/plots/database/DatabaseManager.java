package com.eternalcode.plots.database;

import com.eternalcode.plots.configuration.implementations.PluginConfiguration;
import com.eternalcode.plots.database.warp.FlagWarp;
import com.eternalcode.plots.database.warp.MemberWarp;
import com.eternalcode.plots.database.warp.PlotWarp;
import com.eternalcode.plots.database.warp.RegionWarp;
import com.eternalcode.plots.database.warp.UserWarp;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.logger.Level;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {

    private final PluginConfiguration config;
    private final File dataFolder;
    private final Map<Class<?>, Dao<?, ?>> cachedDao = new ConcurrentHashMap<>();
    private HikariDataSource dataSource;
    private ConnectionSource connection;

    public DatabaseManager(PluginConfiguration config, File dataFolder) {
        this.config = config;
        this.dataFolder = dataFolder;
    }

    public void connect() {

        PluginConfiguration.Storage storage = this.config.storage;
        DatabaseType databaseType = storage.driver;

        String host = storage.settings.hostname;
        String database = storage.settings.database;
        String username = storage.settings.username;
        String password = storage.settings.password;
        int port = storage.settings.port;

        this.dataSource = new HikariDataSource();

        this.dataSource.addDataSourceProperty("cachePrepStmts", true);
        this.dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        this.dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        this.dataSource.addDataSourceProperty("useServerPrepStmts", true);

        this.dataSource.setMaximumPoolSize(5);
        this.dataSource.setUsername(username);
        this.dataSource.setPassword(password);

        Logger.setGlobalLogLevel(Level.WARNING);

        try {
            switch (DatabaseType.valueOf(databaseType.toString().toUpperCase())) {

                case MYSQL -> {
                    this.dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    this.dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
                }

                case MARIADB -> {
                    this.dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
                    this.dataSource.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
                }

                case H2 -> {
                    this.dataSource.setDriverClassName("org.h2.Driver");
                    this.dataSource.setJdbcUrl("jdbc:h2:./" + this.dataFolder + "/database");
                }

                case SQLITE -> {
                    this.dataSource.setDriverClassName("org.sqlite.JDBC");
                    this.dataSource.setJdbcUrl("jdbc:sqlite:" + this.dataFolder + "/database.db");
                }

                case POSTGRESQL -> {
                    this.dataSource.setDriverClassName("org.postgresql.Driver");
                    this.dataSource.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/");
                }

                default -> throw new SQLException("SQL databaseType '" + databaseType + "' not found");
            }

            this.connection = new DataSourceConnectionSource(this.dataSource, this.dataSource.getJdbcUrl());

            TableUtils.createTableIfNotExists(this.connection, FlagWarp.class);
            TableUtils.createTableIfNotExists(this.connection, PlotWarp.class);
            TableUtils.createTableIfNotExists(this.connection, RegionWarp.class);
            TableUtils.createTableIfNotExists(this.connection, MemberWarp.class);
            TableUtils.createTableIfNotExists(this.connection, UserWarp.class);

        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void close() {
        try {
            this.dataSource.close();
            this.connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <T, ID> Dao<T, ID> getDao(Class<T> type) {
        try {
            Dao<?, ?> dao = this.cachedDao.get(type);

            if (dao == null) {
                dao = DaoManager.createDao(this.connection, type);
                this.cachedDao.put(type, dao);
            }

            return (Dao<T, ID>) dao;
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

}
