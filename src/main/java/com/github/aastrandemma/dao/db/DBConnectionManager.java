package com.github.aastrandemma.dao.db;

import com.github.aastrandemma.exception.DBConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
    private static final String DB_NAME = "todo_it";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String JDBC_USER = "root";
    private static final String JDBC_PWD = "1234";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PWD);
        } catch (SQLException e) {
            throw new DBConnectionException("Failed to connect to DB(" + DB_NAME + "), ", e);
        }
    }
}