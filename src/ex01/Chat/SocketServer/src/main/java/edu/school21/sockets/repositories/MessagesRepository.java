package edu.school21.sockets.repositories;

import java.util.List;
import java.util.Date;

public interface MessagesRepository<T> extends CrudRepository<T>{
    List<T> findAllAfterDateTime(Date dateTime);
}

