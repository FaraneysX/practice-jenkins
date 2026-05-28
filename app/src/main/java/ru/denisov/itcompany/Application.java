package ru.denisov.itcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "ru.denisov.core.entities")
@EnableJpaRepositories(basePackages = "ru.denisov.itcompany.repository")
@EnableJpaAuditing
public class Application {
    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
