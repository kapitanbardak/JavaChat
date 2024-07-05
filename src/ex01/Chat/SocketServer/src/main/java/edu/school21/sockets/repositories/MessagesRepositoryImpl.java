package edu.school21.sockets.repositories;

import edu.school21.sockets.models.User;
import edu.school21.sockets.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.*;

public class MessagesRepositoryImpl implements MessagesRepository<Message>{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MessagesRepositoryImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final RowMapper<Message> messageRowMapper = (resultSet, rowNum) -> {
        Message message = new Message();
        message.setIdentifier(resultSet.getLong("id"));
        message.setDateTime(resultSet.getTimestamp("message_datetime"));
        message.setText(resultSet.getString("message_text"));
        long id = resultSet.getLong("author_id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        message.setAuthor(new User(id, username, password));
        return message;
    };

    @Override
    public Optional<Message> findById(Long id) {
        String query = "SELECT * FROM messages \n" +
                "JOIN users ON messages.author_id = users.id\n" +
                "WHERE messages.id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, messageRowMapper).stream().findFirst();
    }

    @Override
    public List<Message> findAll() {
        String query = "SELECT * FROM messages \n" +
        "JOIN users ON messages.author_id = users.id";
        return jdbcTemplate.query(query, messageRowMapper);
    }

    @Override
    public void save(Message message) {
        String query = "INSERT INTO messages (author_id, room_id, message_text, message_datetime) VALUES (?, ?, ?, ?)";

        int[] types = {Types.BIGINT, Types.BIGINT, Types.VARCHAR, Types.TIMESTAMP};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query, types);
        factory.setReturnGeneratedKeys(true);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(factory.newPreparedStatementCreator(Arrays.asList(message.getAuthor().getIdentifier(), message.getRoomIdentifier(), message.getText(), message.getDateTime())), keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        Number id = (Number) keys.get("id");
        message.setIdentifier(id.longValue());
    }

    @Override
    public void update(Message message) {
        String query = "UPDATE messages SET author_id = ?, room_id = ?, message_text = ?, message_datetime = ?  WHERE id = ?";
        jdbcTemplate.update(query, message.getAuthor().getIdentifier(), message.getRoomIdentifier(), message.getText(), message.getDateTime(), message.getIdentifier());
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM messages WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<Message> findAllAfterDateTime(Date dateTime) {
        String query = "SELECT * FROM messages \n" +
                "JOIN users ON messages.author_id = users.id\n" +
                "WHERE messages.message_datetime > ?\n" +
                "ORDER BY messages.message_datetime ASC";
        return jdbcTemplate.query(query, new Object[]{new java.sql.Timestamp(dateTime.getTime())}, messageRowMapper);
    }

}
