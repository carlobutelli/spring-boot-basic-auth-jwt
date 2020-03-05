package com.tech.travel;

import javax.sql.DataSource;

import com.tech.travel.models.Role;
import com.tech.travel.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@SpringBootApplication
public class Application {

    @Value("${documentation.port}")
    private int documentationPort;

    @Bean
    @Primary
    DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .setName("travel-api")
                .build();
    }

    @Bean
    CommandLineRunner init(RoleRepository authoritiesRepository) {
        return args -> {
            authoritiesRepository.save(new Role(Role.ERole.ROLE_USER));
            authoritiesRepository.save(new Role(Role.ERole.ROLE_ADMIN));
            authoritiesRepository.save(new Role(Role.ERole.ROLE_OPS));
            authoritiesRepository.findAll().forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}