package org.pawlak.rentalApp.dao;

import java.util.List;

public interface GenericDao<T> {
    T findById(int id);
    List<T> findAll();
    void insert(T entity);
    void update(T entity);
    void delete(int id);
}
