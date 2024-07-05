package edu.school21.sockets.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import java.sql.SQLException;
import java.util.Optional;

import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class UsersServiceImpl implements UsersService {

    private final UsersRepository<User> userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(@Qualifier("usersRepositoryImpl") UsersRepository<User> userRepository, @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean SignUp(String username, String password) throws SQLException {
        User user = new User();
        user.setUsername(username);
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean SignIn(String username, String password) throws SQLException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPasswordFromDb = user.getPassword();
            return passwordEncoder.matches(password, encodedPasswordFromDb);
        } else {
            return false;
        }
    }

    @Override
    public Optional<User> GetUserByName(String login) throws SQLException {
        return userRepository.findByUsername(login);
    }
}
