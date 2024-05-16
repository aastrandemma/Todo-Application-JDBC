package com.github.aastrandemma.dao;

import com.github.aastrandemma.model.Person;
import com.github.aastrandemma.model.Todo;

import java.util.Collection;

public interface TodoItemsDao extends BaseDao<Todo> {
    Collection<Todo> findByDoneStatus(boolean doneStatus);
    Collection<Todo> findByAssignee(int personId);
    Collection<Todo> findByAssignee(Person person);
    Collection<Todo> findByUnassignedTodoItems();
}