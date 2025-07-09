package com.locked.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConnection {
    Dotenv dotenv = Dotenv.load();
    private final String HOSTNAME = dotenv.get("DB_HOSTNAME");
    private final String USER = dotenv.get("DB_USER");
    private final String PASSWORD = dotenv.get("DB_PASS");

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(HOSTNAME, USER, PASSWORD);
    }
}
