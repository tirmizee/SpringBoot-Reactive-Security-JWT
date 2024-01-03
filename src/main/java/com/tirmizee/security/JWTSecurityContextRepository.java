package com.tirmizee.security;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class JWTSecurityContextRepository implements ServerSecurityContextRepository  {

    private final JWTProvider jwtProvider;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported for stateless.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(6))
                .map(token -> jwtProvider.getClaims(token))
                .flatMap(claims -> {

                    String ipAddress = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
                    if(ipAddress == null) {
                        return Mono.empty();
                    }

                    if(!ipAddress.equals(claims.get("ip", String.class))) {
                        return Mono.empty();
                    }

                    String username = claims.getSubject();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                    return Mono.just(new SecurityContextImpl(authentication));
                });
    }
}
