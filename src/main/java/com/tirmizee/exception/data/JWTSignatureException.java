package com.tirmizee.exception.data;

import org.springframework.security.authentication.AccountStatusException;

public class JWTSignatureException extends AccountStatusException {

    public JWTSignatureException(String msg) {
        super(msg);
    }

}
