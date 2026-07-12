package com.wallpawawqi.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://dpg-d8ui66og4nts73fscqhg-a.oregon-postgres.render.com:5432/basedepatos?sslmode=require";
            
    private static final String USER = "grupo";
    private static final String PASSWORD = "oaC3bS6azsPbbamuipjp5VT7SgwgW8ey";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}
