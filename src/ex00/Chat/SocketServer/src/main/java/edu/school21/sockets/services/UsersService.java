package edu.school21.sockets.services;

import java.sql.SQLException;

public interface UsersService {
    boolean SignUp(String login, String password) throws SQLException;
}