package com.tirmizee.controller.auth;

import com.tirmizee.controller.auth.model.AuthRequest;
import com.tirmizee.controller.auth.model.AuthResponse;
import com.tirmizee.service.AccessTokenService;
import com.tirmizee.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@AllArgsConstructor
@RestController
public class AuthController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final ReactiveAuthenticationManager authenticationManager;

    @PostMapping("/v1/login")
    public Mono<ResponseEntity> login(@RequestBody Mono<AuthRequest> request, ServerWebExchange exchange) {
        String ipAddress = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        return request
                .flatMap(login -> {
                    var usernamePassword = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
                    return authenticationManager.authenticate(usernamePassword);
                })
                .flatMap(authenticated -> {
                    var accessToken = accessTokenService.generateAccessToken(authenticated.getName(), authenticated.getAuthorities(), ipAddress);
                    var refreshToken = refreshTokenService.generateRefreshToken(authenticated.getName(), authenticated.getAuthorities(), ipAddress);
                    return Mono
                            .zip(accessToken, refreshToken)
                            .map(tuple -> new AuthResponse(tuple.getT1(), tuple.getT2()))
                            .map(ResponseEntity::ok);
                });
    }

    @PostMapping("/v2/login")
    public Mono<ResponseEntity> loginBasic(@RequestHeader HttpHeaders headers) {
        String ipAddress = headers.getFirst("X-Forwarded-For");
        return Mono.justOrEmpty(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .map(header -> header.substring(6))
                .map(header -> Base64Utils.decodeFromString(header))
                .map(bytes -> new String(bytes, UTF_8))
                .flatMap(auth -> {
                    var authUsernamePassword = auth.split(":");
                    var usernamePassword = new UsernamePasswordAuthenticationToken(authUsernamePassword[0], authUsernamePassword[1]);
                    return authenticationManager.authenticate(usernamePassword);
                })
                .flatMap(authenticated -> {
                    var accessToken = accessTokenService.generateAccessToken(authenticated.getName(), authenticated.getAuthorities(), ipAddress);
                    var refreshToken = refreshTokenService.generateRefreshToken(authenticated.getName(), authenticated.getAuthorities(), ipAddress);
                    return Mono
                            .zip(accessToken, refreshToken)
                            .map(tuple -> new AuthResponse(tuple.getT1(), tuple.getT2()))
                            .map(ResponseEntity::ok);
                });
    }

    @PostMapping("/v1/refresh/{refresh}")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@PathVariable String refresh, @RequestHeader HttpHeaders headers) {
        String ipAddress = headers.getFirst("X-Forwarded-For");
        return refreshTokenService.refreshToken(refresh, ipAddress)
                .map(authResponse -> ResponseEntity.ok(authResponse))
                .onErrorResume(e -> {
                    log.error("{} -> {} ",e.getClass().getSimpleName(), e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }

}
