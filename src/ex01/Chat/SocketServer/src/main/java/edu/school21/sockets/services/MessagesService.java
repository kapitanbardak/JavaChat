package edu.school21.sockets.services;

import java.sql.SQLException;

import edu.school21.sockets.models.Message;

import java.util.Date;
import java.util.List;
import edu.school21.sockets.models.User;
import org.springframework.stereotype.Component;

@Component
public interface MessagesService {
    long CreateMessage(User user, String text) throws SQLException;
    List<Message> GetMessages(Date date) throws SQLException;
    Date GetLastMessageDate() throws SQLException;
}
