package com.tirmizee.controller.auth;

import com.tirmizee.controller.auth.model.AuthRequest;
import com.tirmizee.controller.auth.model.AuthResponse;
import com.tirmizee.security.JWTProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class AuthController {

    private final JWTProvider jwtProvider;
    private final ReactiveAuthenticationManager authenticationManager;

    @PostMapping("/login/v1")
    public Mono<ResponseEntity> login(@RequestBody Mono<AuthRequest> request) {
        return request.flatMap(login -> {
                    var usernamePassword = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
                    return authenticationManager.authenticate(usernamePassword);
                }).map(authenticated -> {
                    var token = jwtProvider.generateToken(authenticated);
                    var response = new AuthResponse(token);
                    return ResponseEntity.ok(response);
                });
    }

//    @PostMapping("/login/v2")
//    public Mono<ResponseEntity> loginBasic(@RequestHeader Mono<HttpHeaders> headers) {
//        return headers
//                .filter(header -> !header.get(HttpHeaders.AUTHORIZATION).isEmpty())
//                .map(header -> Base64Utils.decodeFromString(String.valueOf(header.get(0))))
//                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
//                .flatMap(auth -> {
//                    var authUsernamePassword = auth.split(":");
//                    var usernamePassword = new UsernamePasswordAuthenticationToken(authUsernamePassword[0], authUsernamePassword[1]);
//                    return authenticationManager.authenticate(usernamePassword);
//                }).map(authenticated -> {
//                    var token = jwtProvider.generateToken(authenticated);
//                    var response = new AuthResponse(token);
//                    return ResponseEntity.ok(response);
//                });
//    }

}
