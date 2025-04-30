package com.example;

import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner bootstrapData(
			RoleRepository roleRepo,
			UserRepository userRepo,
			BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			Role userRole = roleRepo.findByName("ROLE_USER")
					.orElseGet(() -> roleRepo.save(new Role("ROLE_USER")));
			Role adminRole = roleRepo.findByName("ROLE_ADMIN")
					.orElseGet(() -> roleRepo.save(new Role("ROLE_ADMIN")));

			if (userRepo.findByUsername("user").isEmpty()) {
				User user = new User();
				user.setUsername("user");
				user.setPassword(passwordEncoder.encode("password"));
				user.setName("User");
				user.getRoles().add(userRole);
				userRepo.save(user);
			}
			if (userRepo.findByUsername("admin").isEmpty()) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setName("Administrator");
				admin.getRoles().add(adminRole);
				userRepo.save(admin);
			}
		};
	}
}