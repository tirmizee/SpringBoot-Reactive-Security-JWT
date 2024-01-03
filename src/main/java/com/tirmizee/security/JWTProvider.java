package com.tirmizee.security;

import com.tirmizee.exception.data.JWTSignatureException;
import com.tirmizee.property.JWTProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

@AllArgsConstructor
@Component
public class JWTProvider {

    private JWTProperty jwtProperty;

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities, String ip) {

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperty.getAccessExpiration());
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("ip", ip);
        claims.put("authorities", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(jwtProperty.getSecret().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtProperty.getSecret().getBytes(StandardCharsets.UTF_8))
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
