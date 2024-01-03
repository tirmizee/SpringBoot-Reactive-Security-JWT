package com.tirmizee;

import com.tirmizee.repository.UserRepository;
import com.tirmizee.security.JWTProvider;
import com.tirmizee.service.RefreshTokenService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringBootReactiveSecurityJwtApplication {

	public static void main(String[] args) {
		var applicationContext = SpringApplication.run(SpringBootReactiveSecurityJwtApplication.class, args);
		var userRepo = applicationContext.getBean(UserRepository.class);
		var jwtProvider = applicationContext.getBean(JWTProvider.class);
		var passwordEnc = applicationContext.getBean(PasswordEncoder.class);
		var refreshTokenService = applicationContext.getBean(RefreshTokenService.class);
		System.out.println(jwtProvider.generateToken("tirmizee", null, null));
	}

}
