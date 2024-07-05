package edu.school21.sockets.services;

import java.sql.SQLException;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.Message;

import java.util.Date;
import java.util.List;
import edu.school21.sockets.models.User;
import org.springframework.stereotype.Component;

@Component
public interface MessagesService {
    long CreateMessage(User user, Chatroom room, String text) throws SQLException;
    List<Message> GetMessages(Date date) throws SQLException;
    List<Message> GetMessage(long id) throws SQLException;
    Date GetLastMessageDate() throws SQLException;
    List<Message> GetRoomHistory(Chatroom chatroom) throws SQLException;
}
