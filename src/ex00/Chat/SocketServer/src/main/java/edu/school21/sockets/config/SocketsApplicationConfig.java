package edu.school21.sockets.config;

import edu.school21.sockets.services.UsersService;
import edu.school21.sockets.services.UsersServiceImpl;
import org.springframework.context.annotation.PropertySource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import edu.school21.sockets.repositories.UsersRepository;
import edu.school21.sockets.repositories.UsersRepositoryImpl;
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

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "usersRepositoryImpl")
    public UsersRepository usersRepositoryImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        return new UsersRepositoryImpl(dataSource);
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
