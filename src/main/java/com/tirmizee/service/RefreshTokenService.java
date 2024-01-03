package com.tirmizee.service;

import com.tirmizee.controller.auth.model.AuthResponse;
import com.tirmizee.exception.data.UnauthorizedException;
import com.tirmizee.security.JWTProvider;
import com.tirmizee.service.model.RefreshTokenDetail;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import static com.tirmizee.configuration.RedisConfig.REFRESH_TOKEN_PREFIX;

@AllArgsConstructor
@Service
public class RefreshTokenService {

    private final JWTProvider jwtProvider;
    private final ReactiveRedisTemplate<String, RefreshTokenDetail> redisRefreshTokenTemplate;

    public Mono<AuthResponse> refreshToken(String refreshToken, String ip) {
        return getAndDeleteRefreshToken(refreshToken)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new UnauthorizedException("Refresh token is expired or invalid.")))
                .filter(detail -> ip != null && ip.equals(detail.getIp()))
                .switchIfEmpty(Mono.error(new UnauthorizedException("IP mismatch.")))
                .flatMap(refreshTokenDetail -> {
                    String newAccessToken = jwtProvider.generateToken(refreshTokenDetail.getUsername(), refreshTokenDetail.getAuthorities(), ip);
                    return generateRefreshToken(refreshTokenDetail.getUsername(), refreshTokenDetail.getAuthorities(), ip)
                            .map(newRefreshToken -> new AuthResponse(newAccessToken, newRefreshToken));
                });
    }

    public Mono<String> generateRefreshToken(String username, Collection<? extends GrantedAuthority> authorities, String ip) {
        String refreshToken = username + "-" + UUID.randomUUID();
        RefreshTokenDetail refreshTokenDetail = new RefreshTokenDetail();
        refreshTokenDetail.setIp(ip);
        refreshTokenDetail.setUsername(username);
        refreshTokenDetail.setRefreshToken(refreshToken);
        refreshTokenDetail.setAuthorities(authorities);
        return deleteRefreshTokenByUsername(username)
                .then(putRefreshTokenWithExpiry(refreshToken, refreshTokenDetail, Duration.ofMinutes(20)))
                .thenReturn(refreshToken);
    }

    public Mono<Boolean> putRefreshTokenWithExpiry(String refreshToken, RefreshTokenDetail refreshTokenDetail, Duration ttl) {
        return redisRefreshTokenTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, refreshTokenDetail, ttl);
    }

    public Mono<RefreshTokenDetail> getAndDeleteRefreshToken(String refreshToken) {
        return redisRefreshTokenTemplate.opsForValue()
                .getAndDelete(REFRESH_TOKEN_PREFIX + refreshToken);
    }

    public Flux<Long> deleteRefreshTokenByUsername(String username) {
        return redisRefreshTokenTemplate.keys(REFRESH_TOKEN_PREFIX + username + "-*")
                .flatMap(redisRefreshTokenTemplate::delete);
    }

}
