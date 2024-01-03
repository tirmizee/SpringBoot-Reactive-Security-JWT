package com.tirmizee;

import com.tirmizee.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringBootReactiveSecurityJwtApplication implements CommandLineRunner {

	@Autowired
	ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactiveSecurityJwtApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var userRepo = applicationContext.getBean(UserRepository.class);
		var passwordEnc = applicationContext.getBean(PasswordEncoder.class);
	}
}
