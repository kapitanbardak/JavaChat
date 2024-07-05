package edu.school21.sockets.services;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.ChatroomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ChatroomsServiceImpl implements ChatroomsService {

    private final ChatroomsRepository<Chatroom> chatroomsRepository;

    @Autowired
    public ChatroomsServiceImpl(@Qualifier("chatroomsRepositoryImpl") ChatroomsRepository<Chatroom> chatroomsRepository) {
        this.chatroomsRepository = chatroomsRepository;
    }

    @Override
    public long CreateChatroom(String name, User user) throws SQLException {
        Chatroom chatroom = new Chatroom();
        chatroom.setName(name);
        chatroom.setOwner(user);
        chatroomsRepository.save(chatroom);
        return chatroom.getIdentifier();
    }

    @Override
    public List<Chatroom> GetChatrooms() {
        return chatroomsRepository.findAll();
    }

    @Override
    public Optional<Chatroom> GetRoomByName(String name) throws SQLException {
        return chatroomsRepository.findByName(name);
    }
}
