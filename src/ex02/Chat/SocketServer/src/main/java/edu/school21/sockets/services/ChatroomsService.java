package edu.school21.sockets.services;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ChatroomsService {
    long CreateChatroom(String name, User user) throws SQLException;
    List<Chatroom> GetChatrooms() throws SQLException;
    Optional<Chatroom> GetRoomByName(String name) throws SQLException;
}
