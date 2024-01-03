package com.tirmizee.security;

import com.tirmizee.exception.data.UnauthorizedException;
import com.tirmizee.service.AccessTokenService;
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
    private final AccessTokenService accessTokenService;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Not supported for stateless.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return extractBearerToken(exchange.getRequest().getHeaders())
                .flatMap(token -> validateTokenAndGetClaims(token, extractClientIpAddress(exchange)))
                .flatMap(this::buildSecurityContext)
                .switchIfEmpty(Mono.error(new UnauthorizedException("")));
    }

    private Mono<String> extractBearerToken(HttpHeaders headers) {
        return Mono.justOrEmpty(headers.getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7));  // Skip 'Bearer ' prefix
    }

    private Mono<Claims> validateTokenAndGetClaims(String token, String ipAddress) {
        return Mono.fromCallable(() -> jwtProvider.getClaims(token))
                .filter(claims -> ipAddress.equals(claims.get("ip", String.class)))
                .flatMap(claims -> accessTokenService.getAccessToken(claims.getSubject())
                .filter(whitelistToken -> token.equals(whitelistToken))
                .thenReturn(claims));
    }

    private Mono<SecurityContext> buildSecurityContext(Claims claims) {
        String username = claims.getSubject();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, null);
        return Mono.just(new SecurityContextImpl(authentication));
    }

    private String extractClientIpAddress(ServerWebExchange exchange) {
        String ipAddress = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        return ipAddress;
    }
}
