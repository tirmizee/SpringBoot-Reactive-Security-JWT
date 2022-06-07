package com.tirmizee;

import com.tirmizee.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@DataR2dbcTest
class SpringBootReactiveSecurityJwtApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void findByUsername() {
		var userMono = userRepository.findByUsername("tirmizee");
		StepVerifier.create(userMono).expectNextMatches(user -> "tirmizee".equals(user.getUsername())).verifyComplete();
	}

}
