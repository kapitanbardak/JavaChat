package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ChatroomsRepositoryImpl implements ChatroomsRepository<Chatroom> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChatroomsRepositoryImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final RowMapper<Chatroom> chatroomRowMapper = (resultSet, rowNum) -> {
        Chatroom chatroom = new Chatroom();
        chatroom.setIdentifier(resultSet.getLong("id"));
        chatroom.setName(resultSet.getString("name"));
        long id = resultSet.getLong("owner_id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        chatroom.setOwner(new User(id, username, password));
        return chatroom;
    };

    @Override
    public Optional<Chatroom> findById(Long id) {
        String query = "SELECT * FROM chatrooms \n" +
                "JOIN users ON chatrooms.owner_id = users.id\n" +
                "WHERE chatrooms.id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, chatroomRowMapper).stream().findFirst();
    }

    @Override
    public List<Chatroom> findAll() {
        String query = "SELECT * FROM chatrooms \n" +
                "JOIN users ON chatrooms.owner_id = users.id";
        return jdbcTemplate.query(query, chatroomRowMapper);
    }

    @Override
    public void save(Chatroom chatroom) {
        String query = "INSERT INTO chatrooms (name, owner_id) VALUES (?, ?)";

        int[] types = {Types.VARCHAR, Types.BIGINT};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query, types);
        factory.setReturnGeneratedKeys(true);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(factory.newPreparedStatementCreator(Arrays.asList(chatroom.getName(), chatroom.getOwner().getIdentifier())), keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        Number id = (Number) keys.get("id");
        chatroom.setIdentifier(id.longValue());
    }

    @Override
    public void update(Chatroom chatroom) {
        String query = "UPDATE chatrooms SET name = ?, owner_id = ? WHERE id = ?";
        jdbcTemplate.update(query, chatroom.getName(), chatroom.getOwner().getIdentifier(), chatroom.getIdentifier());
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM chatrooms WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public Optional<Chatroom> findByName(String name) {
        String query = "SELECT * FROM chatrooms \n" +
                "JOIN users ON chatrooms.owner_id = users.id\n" +
                "WHERE chatrooms.name = ?";
        return jdbcTemplate.query(query, new Object[]{name}, chatroomRowMapper).stream().findFirst();
    }
}
