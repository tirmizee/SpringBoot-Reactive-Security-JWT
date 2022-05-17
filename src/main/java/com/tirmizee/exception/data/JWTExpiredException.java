package com.tirmizee.exception.data;

import org.springframework.security.authentication.AccountStatusException;

public class JWTExpiredException extends AccountStatusException {

    public JWTExpiredException(String msg) {
        super(msg);
    }

    public JWTExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
