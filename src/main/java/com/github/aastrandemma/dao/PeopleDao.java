package com.github.aastrandemma.dao;

import com.github.aastrandemma.model.Person;

import java.util.Collection;

public interface PeopleDao extends BaseDao<Person> {
    Collection<Person> findByName(String name);
}