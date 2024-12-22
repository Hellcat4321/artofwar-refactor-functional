package com.mygdx.game.db;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBController {
    private final String DB_URL;
    private static Connection connection = null;

    public DBController(String dbName) {
        DB_URL = String.format("jdbc:sqlite:%s", dbName);
    }

    /**
     * Opens a connection to database instance on DB_URL.
     * @throws SQLException
     */
    public void openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DB_URL, config.toProperties());
            connection.setAutoCommit(false);
        }
    }

    /**
     * Closes the current connection to database instance.
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }
}
