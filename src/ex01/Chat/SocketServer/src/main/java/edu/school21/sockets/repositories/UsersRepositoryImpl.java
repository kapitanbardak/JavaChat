package edu.school21.sockets.repositories;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Types;
import java.util.Arrays;
import java.util.Map;

import edu.school21.sockets.models.User;

@Component
public class UsersRepositoryImpl implements UsersRepository<User> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersRepositoryImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
        User user = new User();
        user.setIdendifier(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        return user;
    };

    @Override
    public Optional<User> findById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, userRowMapper).stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, userRowMapper);
    }

    @Override
    public void save(User user) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        int[] types = {Types.VARCHAR, Types.VARCHAR};

        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(query, types);
        factory.setReturnGeneratedKeys(true);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(factory.newPreparedStatementCreator(Arrays.asList(user.getUsername(), user.getPassword())), keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        Number id = (Number) keys.get("id");
        user.setIdendifier(id.longValue());
    }

    @Override
    public void update(User user) {
        String query = "UPDATE users SET username = ? WHERE id = ?";
        jdbcTemplate.update(query, user.getUsername(), user.getIdentifier());
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.query(query, new Object[]{username}, userRowMapper).stream().findFirst();
    }
}
