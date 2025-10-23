package com.GoAero.dao;

import java.util.List;

/**
 * Base interface for Data Access Objects
 * @param <T> The entity type
 * @param <ID> The ID type
 */
public interface BaseDAO<T, ID> {
    
    /**
     * Creates a new entity in the database
     * @param entity The entity to create
     * @return The created entity with generated ID
     */
    T create(T entity);
    
    /**
     * Finds an entity by its ID
     * @param id The ID to search for
     * @return The entity if found, null otherwise
     */
    T findById(ID id);
    
    /**
     * Finds all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Updates an existing entity
     * @param entity The entity to update
     * @return true if update was successful, false otherwise
     */
    boolean update(T entity);
    
    /**
     * Deletes an entity by its ID
     * @param id The ID of the entity to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(ID id);
    
    /**
     * Checks if an entity exists by its ID
     * @param id The ID to check
     * @return true if entity exists, false otherwise
     */
    boolean exists(ID id);
    
    /**
     * Counts the total number of entities
     * @return The total count
     */
    long count();
}
