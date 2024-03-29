package com.tirmizee.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JWTProperty {
    private String secret;
    private long accessExpiration;
    private long refreshExpiration;
}
