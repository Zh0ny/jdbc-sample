package br.com.dio;

import org.flywaydb.core.Flyway;

import br.com.dio.persistence.EmployeeDAO;

public class Main {

    private final static EmployeeDAO employeeDAO = new EmployeeDAO();
    public static void main(String args[]) {
        var flyway = Flyway.configure()
            .dataSource("jdbc:mysql://localhost:3306/jdbc-sample", "{your_username}", "{your_password}")
            .load();

        flyway.migrate();
    }

}
