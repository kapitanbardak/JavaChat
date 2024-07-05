package edu.school21.sockets.repositories;

import java.util.List;
import java.util.Optional;

public interface ChatroomsRepository<T> extends CrudRepository<T>{
    Optional<T> findByName(String name);
}
