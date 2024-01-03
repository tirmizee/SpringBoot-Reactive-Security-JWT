package com.tirmizee.service;

import com.tirmizee.property.JWTProperty;
import com.tirmizee.security.JWTProvider;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;

import static com.tirmizee.configuration.RedisConfig.ACCESS_TOKEN_PREFIX;

@AllArgsConstructor
@Service
public class AccessTokenService {

    private final JWTProvider jwtProvider;
    private final JWTProperty jwtProperty;
    private final ReactiveRedisTemplate<String, String> redisStringTemplate;

    public Mono<String> generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities, String ip) {
        String accessToken = jwtProvider.generateToken(username, authorities, ip);
        return putAccessTokenWithExpiry(username, accessToken, Duration.ofMillis(jwtProperty.getExpiration()))
                .thenReturn(accessToken);
    }

    public Mono<Boolean> putAccessTokenWithExpiry(String username, String accessToken, Duration ttl) {
        return redisStringTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + username, accessToken, ttl);
    }

    public Mono<String> getAccessToken(String username) {
        return redisStringTemplate.opsForValue().getAndDelete(ACCESS_TOKEN_PREFIX + username);
    }

}
