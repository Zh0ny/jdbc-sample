package br.com.dio.persistence;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;

import com.mysql.cj.jdbc.StatementImpl;

import br.com.dio.persistence.entity.EmployeeEntity;

public class EmployeeDAO {

    public void insert (final EmployeeEntity entity){
        try(
            var connection = ConnectionUtil.getConnection();
            var statement = connection.createStatement();
        ){
            var sql = "INSERT INTO employees (name, salary, birthDate) values ('" +
                entity.getName() + "', " +
                entity.getSalary().toString() + ", " +
                "'" + formatOffsetDateTime(entity.getBirthDate()) + "' )";
            statement.executeUpdate(sql);
            if (statement instanceof StatementImpl impl)
                entity.setId(impl.getLastInsertID());
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void update (final EmployeeEntity entity){
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement()
        ){
            var sql_querry = "UPDATE employees SET " +
                "name      = '" + entity.getName() + "', " +
                "salary    = " + entity.getSalary().toString() + ", " +
                "birthDate = '" + formatOffsetDateTime(entity.getBirthDate()) + "' " +
                "WHERE id  = " + entity.getId();
            statement.executeUpdate(sql_querry);
            
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void delete (final Long id){
        try(
                    var connection = ConnectionUtil.getConnection();
                    var statement = connection.createStatement()
        ){
            var sql = "DELETE FROM employees WHERE id = " + id;
            statement.executeUpdate(sql);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public List<EmployeeEntity> findAll (){
        List<EmployeeEntity> entities = new ArrayList<>();
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement()
        ){
            statement.executeQuery("SELECT * FROM employees ORDER BY name");
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var entity = new EmployeeEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setSalary(resultSet.getBigDecimal("salary"));
                var birthdayInstant = resultSet.getTimestamp("birthDate").toInstant();
                entity.setBirthDate(OffsetDateTime.ofInstant(birthdayInstant, UTC));
                entities.add(entity);
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return entities;
    }

    public EmployeeEntity findById (final long id){
        EmployeeEntity entity = new EmployeeEntity();
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement()
        ){
            statement.executeQuery("SELECT * FROM employees WHERE id = " + id);
            var resultSet = statement.getResultSet();
            if(resultSet.next()){
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setSalary(resultSet.getBigDecimal("salary"));
                var birthdayInstant = resultSet.getTimestamp("birthDate").toInstant();
                entity.setBirthDate(OffsetDateTime.ofInstant(birthdayInstant, UTC));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return entity;
    }

    private String formatOffsetDateTime(final OffsetDateTime dateTime){
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
