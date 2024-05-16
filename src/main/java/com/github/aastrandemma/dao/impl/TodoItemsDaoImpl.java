package com.github.aastrandemma.dao.impl;

import com.github.aastrandemma.dao.TodoItemsDao;
import com.github.aastrandemma.exception.MySQLException;
import com.github.aastrandemma.model.Person;
import com.github.aastrandemma.model.Todo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Types.*;

public class TodoItemsDaoImpl implements TodoItemsDao {
    private static TodoItemsDaoImpl instance;
    private final Connection connection;

    private TodoItemsDaoImpl(Connection connection) {
        this.connection = connection;
    }

    public static TodoItemsDaoImpl getInstance(Connection connection) {
        if (instance == null) {
            instance = new TodoItemsDaoImpl(connection);
        }
        return instance;
    }

    @Override
    public Todo create(Todo todo) {
        String insertQuery = "INSERT INTO todo_item (title, description, deadline, done, assignee_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, todo.getTitle());
            preparedStatement.setString(2, todo.getDescription());
            if (todo.getDeadline() == null) {
                preparedStatement.setNull(3, DATE);
            } else {
                preparedStatement.setDate(3, Date.valueOf(todo.getDeadline()));
            }
            preparedStatement.setBoolean(4, todo.isDone());
            if (todo.getAssignee() == null) {
                preparedStatement.setNull(5, INTEGER);
            } else {
                preparedStatement.setInt(5, todo.getAssignee().getId());
            }

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                String errorMessage = "Insert operation for todo_item table failed, no rows affected.";
                throw new MySQLException(errorMessage);
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    todo.setId(generatedKeys.getInt(1));
                    return todo;
                } else {
                    String errorMessage = "Failed to generate key for insert operation todo_item table.";
                    throw new MySQLException(errorMessage);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Error occurred during insert operation todo_item table: ";
            throw new MySQLException(errorMessage, e);
        }
    }

    @Override
    public Collection<Todo> findAll() {
        String selectQuery = "SELECT * FROM todo_item";
        Collection<Todo> todoItems = new ArrayList<>();
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery)
        ) {
            while (resultSet.next()) {
                todoItems.add(extractTodoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from todo_item table, for findAll(), ";
            throw new MySQLException(errorMessage, e);
        }
        return todoItems;
    }

    @Override
    public Todo findById(int id) {
        String selectQuery = "SELECT * FROM todo_item WHERE todo_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractTodoFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from todo_item table, for findById() with id: " + id + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return null;
    }

    @Override
    public Todo update(Todo todo) {
        String updateQuery = "UPDATE todo_item SET title = ?, description = ?, deadline = ?, done = ?, assignee_id = ? WHERE todo_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            connection.setAutoCommit(false);
            preparedStatement.setString(1, todo.getTitle());
            preparedStatement.setString(2, todo.getDescription());
            preparedStatement.setDate(3, Date.valueOf(todo.getDeadline()));
            preparedStatement.setBoolean(4, todo.isDone());
            preparedStatement.setInt(5, todo.getAssignee().getId());
            preparedStatement.setInt(6, todo.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                connection.setAutoCommit(true);
                return todo;
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            String errorMessage = "Failed update() for todo_item table with id: " + todo.getId() + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return todo;
    }

    @Override
    public boolean deleteById(int id) {
        int rowsDeleted = -1;
        String deleteQuery = "DELETE FROM todo_item WHERE todo_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
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
            String errorMessage = "Failed to delete data in todo_item table, for deleteById() with id: " + id + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return false;
    }

    @Override
    public Collection<Todo> findByDoneStatus(boolean doneStatus) {
        Collection<Todo> todoItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM todo_item WHERE done = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setBoolean(1, doneStatus);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    todoItems.add(extractTodoFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from todo_item table, for findByDoneStatus() with status: " + doneStatus + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return todoItems;
    }

    @Override
    public Collection<Todo> findByAssignee(int personId) {
        Collection<Todo> todoItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM todo_item WHERE assignee_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, personId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    todoItems.add(extractTodoFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from todo_item table, for findByAssignee() with assignee_id: " + personId + ", ";
            throw new MySQLException(errorMessage, e);
        }
        return todoItems;
    }

    @Override
    public Collection<Todo> findByAssignee(Person person) {
        return findByAssignee(person.getId());
    }

    @Override
    public Collection<Todo> findByUnassignedTodoItems() {
        Collection<Todo> todoItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM todo_item WHERE assignee_id IS NULL";
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery)
        ) {
            while (resultSet.next()) {
                todoItems.add(extractTodoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            String errorMessage = "Failed to fetch data from todo_item table, for findByUnassignedTodoItems(), ";
            throw new MySQLException(errorMessage, e);
        }
        return todoItems;
    }

    private Todo extractTodoFromResultSet(ResultSet resultSet) throws SQLException {
        int todoId = resultSet.getInt("todo_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");

        LocalDate deadline = null;
        if (resultSet.getObject("deadline") != null) {
            deadline = resultSet.getDate("deadline").toLocalDate();
        }

        boolean doneStatus = resultSet.getBoolean("done");

        Person person = null;
        if (resultSet.getObject("assignee_id") != null) {
            person = PeopleDaoImpl.getInstance(connection).findById(resultSet.getInt("assignee_id"));
        }

        if (person == null) {
            return new Todo(todoId, title, description, deadline, doneStatus);
        } else {
            return new Todo(todoId, title, description, deadline, doneStatus, person);
        }
    }
}