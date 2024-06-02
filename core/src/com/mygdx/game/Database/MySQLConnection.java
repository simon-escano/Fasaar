package com.mygdx.game.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection c;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("localhost connection success");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return c;
    }

    public static Connection getDatabase() {
        Connection d = null;
        try {
            d = MySQLConnection.getConnection();
            Statement statement = d.createStatement();
            String query = "CREATE DATABASE IF NOT EXISTS dbFasaar";
            int databaseCreated = statement.executeUpdate(query);
            System.out.println("Database created: " + databaseCreated);
            d = DriverManager.getConnection(URL + "dbFasaar", USERNAME, PASSWORD);
            System.out.println("DB connection success");
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return d;
    }
}
