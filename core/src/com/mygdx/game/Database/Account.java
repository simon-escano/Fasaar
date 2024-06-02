package com.mygdx.game.Database;

import java.sql.*;
import java.util.ArrayList;

public class Account implements Runnable {
    @Override
    public void run() {
        try (Connection c = MySQLConnection.getDatabase();
             Statement statement = c.createStatement();
        ) {
            String query = "CREATE TABLE IF NOT EXISTS tbluser (" +
                "userID INT PRIMARY KEY AUTO_INCREMENT," +
                "username VARCHAR(50) NOT NULL," +
                "password TEXT NOT NULL," +
                "color TEXT NOT NULL," +
                "email TEXT NOT NULL)";
            statement.execute(query);
            System.out.println("Table [user] created successfully!");
            query = "CREATE TABLE IF NOT EXISTS tblplayer (" +
                "charID INT PRIMARY KEY AUTO_INCREMENT," +
                "userID INT," +
                "FOREIGN KEY (userID) REFERENCES tbluser(userID)," +
                "positionx FLOAT NOT NULL," +
                "positionz FLOAT NOT NULL)";
            statement.execute(query);
            System.out.println("Table [player] created successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> findUser(String username, int password) throws SQLException {
        try (Connection c = MySQLConnection.getDatabase();
             Statement statement = c.createStatement()){
            String query = "SELECT userID, color FROM tbluser where username='"+username+"' and password='"+password+"'";
            ResultSet res = statement.executeQuery(query);
            if (res.next()){
                ArrayList<String> list = new ArrayList<>();
                int id = res.getInt("userID");
                list.add(String.valueOf(id));
                String color = res.getString("color");
                list.add(color);
                return list;
            }
            else {
                System.out.println("Something went wrong, please check your credentials.");
                return null;
            }
        }
    }

    public static void createUser(String username, String password, String email, String color) throws SQLException {
        try (Connection c = MySQLConnection.getDatabase();
             PreparedStatement userStatement = c.prepareStatement(
                     "INSERT INTO tbluser (username, password, color, email) VALUES (?, ?, ?, ?)")) {
            userStatement.setString(1, username);
            userStatement.setInt(2, password.hashCode());
            userStatement.setString(3, color);
            userStatement.setString(4, email);
            userStatement.executeUpdate();
        }
    }
}
