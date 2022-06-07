package com.tirmizee.security;

import com.tirmizee.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class UserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<org.springframework.security.core.userdetails.UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(userEntity -> {
                    UserDetails user = UserDetails.builder()
                        .username(username)
                        .password(userEntity.getPassword())
                        .enabled(userEntity.isEnabled())
                        .build();
                    return Mono.just(user);
                });
    }
}
