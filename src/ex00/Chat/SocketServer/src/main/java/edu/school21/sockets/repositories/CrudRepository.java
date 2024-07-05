package edu.school21.sockets.repositories;

import java.util.Optional;
import java.util.List;

public interface CrudRepository<T> {
    Optional<T> findById(Long id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    void delete(Long id);
}