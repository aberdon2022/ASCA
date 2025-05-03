package org.chatproject.ascp;

import org.chatproject.ascp.models.Role;
import org.chatproject.ascp.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AscaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AscaApplication.class, args);
    }

    @Bean
    CommandLineRunner run(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("USER"));
                roleRepository.save(new Role("ADMIN"));
            }
        };
    }
}
