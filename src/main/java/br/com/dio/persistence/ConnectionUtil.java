package br.com.dio.persistence;

import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.AccessLevel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionUtil {
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(System.getenv("db_url"), System.getenv("db_username"), System.getenv("db_password"));
    }
}
