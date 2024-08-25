package com.lollychat.securingweb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public JdbcUserDetailsManager userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("select username, password, enabled from users where username = ?");
        manager.setAuthoritiesByUsernameQuery("select username, authority from authorities where username = ?");
        return manager;
    }

    @Bean
    public CommandLineRunner initUsers(JdbcUserDetailsManager userDetailsService, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userDetailsService.userExists("user")) {
                UserDetails user = User.withUsername("user")
                        .password(passwordEncoder.encode("password")) // Шифруємо пароль за допомогою PasswordEncoder
                        .build();
                userDetailsService.createUser(user);
            }
        };
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll() // Дозволити доступ до всіх запитів
                .and()
                .csrf().disable() // Вимкнути CSRF захист
                .formLogin().disable() // Вимкнути форму логіну
                .httpBasic().disable(); // Вимкнути базову автентифікацію

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/chatdata") // Замініть на вашу базу даних
                .username("postgres")
                .password("123qweasdzxc")
                .driverClassName("org.postgresql.Driver")
                .build();
    }
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(465);

        mailSender.setUsername("chatyvalb@gmail.com");
        mailSender.setPassword("rhpv nrkq azyo zmcf");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.debug", "true"); // Enable debugging to view detailed logs

        return mailSender;
    }

}
