package com.tirmizee.service.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class RefreshTokenDetail {
    private String username;
    private String ip;
    private String refreshToken;
    private Collection<? extends GrantedAuthority> authorities;
}
