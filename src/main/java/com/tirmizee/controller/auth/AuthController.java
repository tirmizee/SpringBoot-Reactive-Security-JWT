package com.tirmizee.controller.auth;

import com.tirmizee.controller.auth.model.AuthRequest;
import com.tirmizee.controller.auth.model.AuthResponse;
import com.tirmizee.security.JWTProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.nio.charset.StandardCharsets.UTF_8;

@AllArgsConstructor
@RestController
public class AuthController {

    private final JWTProvider jwtProvider;
    private final ReactiveAuthenticationManager authenticationManager;

    @PostMapping("/v1/login")
    public Mono<ResponseEntity> login(@RequestBody Mono<AuthRequest> request, ServerWebExchange exchange) {
        String ipAddress = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        return request.flatMap(login -> {
                    var usernamePassword = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
                    return authenticationManager.authenticate(usernamePassword);
                }).map(authenticated -> {
                    var token = jwtProvider.generateToken(authenticated, ipAddress);
                    var response = new AuthResponse(token);
                    return ResponseEntity.ok(response);
                });
    }

    @PostMapping("/v2/login")
    public Mono<ResponseEntity> loginBasic(@RequestHeader HttpHeaders headers) {
        return Mono.justOrEmpty(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .map(header -> header.substring(6))
                .map(header -> Base64Utils.decodeFromString(header))
                .map(bytes -> new String(bytes, UTF_8))
                .flatMap(auth -> {
                    var authUsernamePassword = auth.split(":");
                    var usernamePassword = new UsernamePasswordAuthenticationToken(authUsernamePassword[0], authUsernamePassword[1]);
                    return authenticationManager.authenticate(usernamePassword);
                })
                .map(authenticated -> {
                    String ipAddress = headers.getFirst("X-Forwarded-For");
                    var token = jwtProvider.generateToken(authenticated, ipAddress);
                    var response = new AuthResponse(token);
                    return ResponseEntity.ok(response);
                });
    }

}
