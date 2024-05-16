package com.github.aastrandemma.dao.impl;

import com.github.aastrandemma.dao.PeopleDao;
import com.github.aastrandemma.exception.MySQLException;
import com.github.aastrandemma.model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class PeopleDaoImpl implements PeopleDao {
    private static PeopleDaoImpl instance;
    private final Connection connection;

    private PeopleDaoImpl(Connection connection) {
        this.connection = connection;
    }

    public static PeopleDaoImpl getInstance(Connection connection) {
        if (instance == null) {
            instance = new PeopleDaoImpl(connection);
        }
        return instance;
    }

    @Override
    public Person create(Person person) {
        String insertQuery = "INSERT INTO person (first_name, last_name) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, person.getFirstName());
            preparedStatement.setString(2, person.getLastName());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                String errorMessage = "Insert operation for person table failed, no rows affected.";
                throw new MySQLException(errorMessage);
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    person.setId(generatedKeys.getInt(1));
                    return person;
                } else {
                    String errorMessage = "Failed to generate key for insert operation person table.";
                    throw new MySQLException(errorMessage);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Error occurred during insert operation person table: ";
            throw new MySQLException(errorMessage, e);
        }
    }

    @Override
    public Collection<Person> findAll() {
        String selectQuery = "SELECT * FROM person";
        Collection<Person> people = new ArrayList<>();
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery)
        ) {
            while (resultSet.next()) {
                people.add(extractPersonFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from person table, for findAll(), ";
            throw new MySQLException(errorMessage, e);
        }
        return people;
    }

    @Override
    public Person findById(int id) {
        String selectQuery = "SELECT * FROM person WHERE person_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractPersonFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from person table, for findById() with id: " + id + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return null;
    }

    @Override
    public Person update(Person person) {
        String updateQuery = "UPDATE person SET first_name = ?, last_name = ? WHERE person_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, person.getFirstName());
            preparedStatement.setString(2, person.getLastName());
            preparedStatement.setInt(3, person.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                connection.setAutoCommit(true);
                return person;
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            String errorMessage = "Failed update() for person table with id: " + person.getId() + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return person;
    }

    @Override
    public boolean deleteById(int id) {
        int rowsDeleted = -1;
        String deletePersonQuery = "DELETE FROM person WHERE person_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deletePersonQuery)) {
            connection.setAutoCommit(false);
            preparedStatement.setInt(1, id);

            rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                connection.setAutoCommit(true);
                return true;
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to delete data in person table, for deleteById() with id: " + id + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return false;
    }

    @Override
    public Collection<Person> findByName(String name) {
        Collection<Person> people = new ArrayList<>();
        String selectQuery = "SELECT * FROM person WHERE first_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, name);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    people.add(extractPersonFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from person table, for findByName() with name: " + name + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return people;
    }

    private Person extractPersonFromResultSet(ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getInt("person_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        );
    }
}