package br.com.dio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.stream.Stream;

import org.flywaydb.core.Flyway;
import net.datafaker.Faker;

import br.com.dio.persistence.EmployeeAuditDAO;
import br.com.dio.persistence.EmployeeParamDAO;
import br.com.dio.persistence.entity.EmployeeEntity;

import static java.time.ZoneOffset.UTC;

public class Main {

    private final static EmployeeParamDAO employeeDAO = new EmployeeParamDAO();
    private final static EmployeeAuditDAO employeeAuditDAO = new EmployeeAuditDAO();
    private final static Faker faker = new Faker(Locale.of("pt", "BR"));
    public static void main(String args[]) {
        var flyway = Flyway.configure()
            .dataSource(System.getenv("db_url"), System.getenv("db_username"), System.getenv("db_password"))
            .load();
        flyway.migrate();

        var entities = Stream.generate(() -> {
            var employee = new EmployeeEntity();
            employee.setName(faker.name().fullName());
            employee.setSalary(new BigDecimal(faker.number().digits(4)));
            employee.setBirthDate(OffsetDateTime.of(LocalDate.now().minusYears(faker.number().numberBetween(40, 20)), LocalTime.MIN, UTC));
            return employee;
        }).limit(10000).toList();

        employeeDAO.insert(entities);

    }

}
