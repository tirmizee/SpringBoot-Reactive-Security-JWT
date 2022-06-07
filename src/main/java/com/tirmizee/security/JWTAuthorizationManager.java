package com.tirmizee.security;

import com.tirmizee.exception.data.JWTExpiredException;
import com.tirmizee.exception.data.JWTSignatureException;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.SignatureException;
import java.util.Date;

@AllArgsConstructor
@Component
public class JWTAuthorizationManager implements ReactiveAuthenticationManager {

    private final JWTProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication)  {

            String token = authentication.getCredentials().toString();

            return Mono.just(token)
                    .map(jwt -> {
                        Claims claims = jwtProvider.getClaims(jwt);
                        String username = getUsername(claims);
                        return new UsernamePasswordAuthenticationToken(username, null, null);
                    });
    }

    private String getUsername(Claims claims) {
        return claims.getSubject();
    }

    private Date getExpiration(Claims claims) {
        return claims.getExpiration();
    }

    private Boolean isExpired(Claims claims) {
        return getExpiration(claims).before(new Date());
    }

}
