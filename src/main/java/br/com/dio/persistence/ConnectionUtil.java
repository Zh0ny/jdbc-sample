package br.com.dio.persistence;

import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionUtil {
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-sample", "your_username", "your_password");
    }
}
