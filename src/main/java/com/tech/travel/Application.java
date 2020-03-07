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
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
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

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tech.travel"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot Basic Auth Jwt")
                .description("Basic authentication service with jwt authorization")
                .contact(new Contact("Carlo Butelli", "www.carlobutelli.com", "dev.butelli@gmail.com"))
                .license("MIT License")
                .version("1.0.0")
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}