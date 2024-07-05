package edu.school21.sockets.repositories;

import java.util.Optional;

public interface UsersRepository<T> extends CrudRepository<T>{
    Optional<T> findByUsername(String username);
}
