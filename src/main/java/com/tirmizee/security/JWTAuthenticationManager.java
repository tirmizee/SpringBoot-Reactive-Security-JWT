package com.tirmizee.security;

import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public class JWTAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

    public JWTAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return super.retrieveUser(username);
    }

}
