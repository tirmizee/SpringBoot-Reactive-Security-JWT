package com.tirmizee.security;

import com.tirmizee.exception.data.JWTSignatureException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

@Component
public class JWTProvider {

    private String key = "11111111111111111111111111111111";

    public String generateToken(Authentication authentication) {

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000);
        Claims claims = Jwts.claims().setSubject(username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new JWTSignatureException(e.getMessage());
        }
    }

    public String getUsername(String token) {
        return getUsername(getClaims(token));
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    public Date getExpiration(Claims claims) {
        return claims.getExpiration();
    }

    public Boolean isExpired(Claims claims) {
        return getExpiration(claims).before(new Date());
    }

}
