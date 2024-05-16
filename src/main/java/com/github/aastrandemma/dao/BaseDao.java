package com.github.aastrandemma.dao;

import java.util.Collection;

public interface BaseDao<T> {
    T create(T t);
    Collection<T> findAll();
    T findById(int id);
    T update(T t);
    boolean deleteById(int id);
}