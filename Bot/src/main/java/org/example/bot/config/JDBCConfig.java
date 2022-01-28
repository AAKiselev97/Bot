package org.example.bot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConfig {
    private final static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    private JDBCConfig() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void init(Properties properties) {
        config.setJdbcUrl(properties.getProperty("databaseConnection"));
        config.setUsername(properties.getProperty("databaseLogin"));
        config.setPassword(properties.getProperty("databasePassword"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }
}
