package com.tirmizee.service;

import com.tirmizee.controller.auth.model.AuthResponse;
import com.tirmizee.exception.data.UnauthorizedException;
import com.tirmizee.security.JWTProvider;
import com.tirmizee.service.model.RefreshTokenDetail;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class RefreshTokenService {

    private final JWTProvider jwtProvider;
    private final ReactiveRedisTemplate<String, RefreshTokenDetail> reactiveRedisTemplate;

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
        String refreshToken = UUID.randomUUID().toString();
        RefreshTokenDetail refreshTokenDetail = new RefreshTokenDetail();
        refreshTokenDetail.setIp(ip);
        refreshTokenDetail.setUsername(username);
        refreshTokenDetail.setRefreshToken(refreshToken);
        refreshTokenDetail.setAuthorities(authorities);
        return putRefreshTokenWithExpiry(refreshToken, refreshTokenDetail, Duration.ofMinutes(20))
                .thenReturn(refreshToken);
    }

    public Mono<Boolean> putRefreshTokenWithExpiry(String refreshToken, RefreshTokenDetail refreshTokenDetail, Duration ttl) {
        return reactiveRedisTemplate.opsForValue().set("refresh_token:" +  refreshToken, refreshTokenDetail, ttl);
    }

    public Mono<RefreshTokenDetail> getAndDeleteRefreshToken(String refreshToken) {
        return reactiveRedisTemplate.opsForValue()
                .getAndDelete("refresh_token:" +  refreshToken);
    }

}
