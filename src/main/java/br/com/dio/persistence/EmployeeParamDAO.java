package br.com.dio.persistence;

import br.com.dio.persistence.entity.EmployeeEntity;
import com.mysql.cj.jdbc.StatementImpl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;

public class EmployeeParamDAO {

    public void insert(final EmployeeEntity entity){
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement(
                        "INSERT INTO employees (name, salary, birthDate) values (?, ?, ?);"
                )
        ){
            statement.setString(1, entity.getName());
            statement.setBigDecimal(2, entity.getSalary());
            statement.setTimestamp(3,
                    Timestamp.valueOf(entity.getBirthDate().atZoneSimilarLocal(UTC).toLocalDateTime())
            );
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl)
                entity.setId(impl.getLastInsertID());
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insert(final List<EmployeeEntity> entities){
        try(var connection = ConnectionUtil.getConnection()){
            var sql = "INSERT INTO employees (name, salary, birthDate) values (?, ?, ?);";
            try(var statement = connection.prepareStatement(sql)){
                connection.setAutoCommit(false);
                for (int i = 0; i < entities.size(); i++) {
                    statement.setString(1, entities.get(i).getName());
                    statement.setBigDecimal(2, entities.get(i).getSalary());
                    var timestamp = Timestamp.valueOf(entities.get(i).getBirthDate().atZoneSimilarLocal(UTC)
                            .toLocalDateTime());
                    statement.setTimestamp(3, timestamp);
                    statement.addBatch();
                    if (i % 1000 == 0 || i == entities.size() -1) statement.executeBatch();
                }
                connection.commit();
            }catch (SQLException ex){
                connection.rollback();
                ex.printStackTrace();
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void insertWithProcedure(final EmployeeEntity insert){
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareCall(
                        "call prc_insert_employee(?, ?, ?, ?);"
                )
        ){
            statement.registerOutParameter(1, Types.BIGINT);
            statement.setString(2, insert.getName());
            statement.setBigDecimal(3, insert.getSalary());
            statement.setTimestamp(4,
                    Timestamp.valueOf(insert.getBirthDate().atZoneSimilarLocal(UTC).toLocalDateTime())
            );
            statement.execute();
            insert.setId(statement.getLong(1));
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void update(final EmployeeEntity entity){
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement(
                        "UPDATE employees set name = ?, salary = ?, birthDate = ? WHERE id = ?"
                )
        ){
            statement.setString(1, entity.getName());
            statement.setBigDecimal(2, entity.getSalary());
            statement.setTimestamp(3,
                    Timestamp.valueOf(entity.getBirthDate().atZoneSimilarLocal(UTC).toLocalDateTime())
            );
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            System.out.printf("Foram afetados %s registros na base de dados", statement.getUpdateCount());
            if (statement instanceof StatementImpl impl)
                entity.setId(impl.getLastInsertID());
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void delete(final long id){
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement("DELETE FROM employees WHERE id = ?")
        ){
            statement.setLong(1, id);
            statement.executeUpdate();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public List<EmployeeEntity> findAll(){
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
                var birthDateInstant = resultSet.getTimestamp("birthDate").toInstant();
                entity.setBirthDate(OffsetDateTime.ofInstant(birthDateInstant, UTC));
                entities.add(entity);
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return entities;
    }

    public EmployeeEntity findById(final long id){
        var entity = new EmployeeEntity();
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.prepareStatement("SELECT * FROM employees WHERE id = ?")
        ){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setSalary(resultSet.getBigDecimal("salary"));
                var birthDateInstant = resultSet.getTimestamp("birthDate").toInstant();
                entity.setBirthDate(OffsetDateTime.ofInstant(birthDateInstant, UTC));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return entity;
    }

    private String formatOffsetDateTime(final OffsetDateTime dateTime){
        var utcDatetime = dateTime.withOffsetSameInstant(UTC);
        return utcDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
