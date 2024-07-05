package edu.school21.sockets.config;

import edu.school21.sockets.repositories.*;
import edu.school21.sockets.services.*;
import org.springframework.context.annotation.PropertySource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@PropertySource("classpath:db.properties")
public class SocketsApplicationConfig {

    @Bean(name = "usersServiceImpl")
    public UsersService usersServiceImpl(@Qualifier("usersRepositoryImpl") UsersRepository usersRepository, @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        return new UsersServiceImpl(usersRepository, passwordEncoder);
    }

    @Bean(name = "messagesServiceImpl")
    public MessagesService messagesService(@Qualifier("messagesRepositoryImpl") MessagesRepository messagesRepository) {
        return new MessagesServiceImpl(messagesRepository);
    }

    @Bean(name = "chatroomsServiceImpl")
    public ChatroomsService chatroomsService(@Qualifier("chatroomsRepositoryImpl") ChatroomsRepository chatroomsRepository) {
        return new ChatroomsServiceImpl(chatroomsRepository);
    }

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "usersRepositoryImpl")
    public UsersRepository usersRepositoryImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        return new UsersRepositoryImpl(dataSource);
    }

    @Bean(name = "messagesRepositoryImpl")
    public MessagesRepository messagesRepository(@Qualifier("hikariDataSource") DataSource dataSource) {
        return new MessagesRepositoryImpl(dataSource);
    }

    @Bean(name = "chatroomsRepositoryImpl")
    public ChatroomsRepository chatroomsRepository(@Qualifier("hikariDataSource") DataSource dataSource) {
        return new ChatroomsRepositoryImpl(dataSource);
    }

    @Bean
    public HikariDataSource hikariDataSource(
            @Value("${db.driver.name}") String driverClassName,
            @Value("${db.url}") String jdbcUrl,
            @Value("${db.user}") String username,
            @Value("${db.password}") String password) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        return hikariDataSource;
    }
}
