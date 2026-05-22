package com.wallpawawqi.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://dpg-d7n18ul7vvec738s3u30-a.oregon-postgres.render.com/BaseDePatos?sslmode=require";

    private static final String USER = "tobi";
    private static final String PASSWORD = "kmwfcC6CLYOEhi2m8F3Vu8Y3cXU4Auxo";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}