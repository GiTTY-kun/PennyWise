package com.pennywise.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic data-access interface: T is the entity type, ID the type of its primary key.
 * Every table in PennyWise has a DAO that implements this interface.
 */
public interface Dao<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    T save(T entity);          // insert or update; returns the saved entity
    void delete(ID id);
}
