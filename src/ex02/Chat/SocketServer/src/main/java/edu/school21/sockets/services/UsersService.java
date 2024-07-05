package edu.school21.sockets.services;

import java.sql.SQLException;
import java.util.Optional;

import edu.school21.sockets.models.User;

public interface UsersService {
    boolean SignUp(String login, String password) throws SQLException;
    boolean SignIn(String login, String password) throws SQLException;
    Optional<User> GetUserByName(String login) throws SQLException;
}