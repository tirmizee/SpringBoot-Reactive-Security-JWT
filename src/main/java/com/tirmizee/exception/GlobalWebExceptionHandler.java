package com.tirmizee.exception;

import com.tirmizee.exception.data.JWTExpiredException;
import com.tirmizee.exception.data.JWTSignatureException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GlobalWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        String message = ex.getMessage();

        if(ex instanceof LockedException){
            message = "account is locked";
        }

        if(ex instanceof AccountExpiredException){
            message =  "account is expired";
        }

        if(ex instanceof JWTExpiredException) {
            message = "JWT is expired";
        }

        if(ex instanceof JWTSignatureException) {
            message = "JWT signature does not match";
        }

        return renderResponseJson(exchange.getResponse(), message);
    }

    private Mono<Void> renderResponseJson(ServerHttpResponse serverResponse, Object data) {
        return renderResponseJson(serverResponse, data, null, HttpStatus.OK);
    }

    private Mono<Void> renderResponseJson(ServerHttpResponse serverResponse, Object data, HttpStatus status) {
        return renderResponseJson(serverResponse, data, null, status);
    }

    private Mono<Void> renderResponseJson(ServerHttpResponse serverResponse, Object data, MultiValueMap<String, String> headers) {
        return renderResponseJson(serverResponse, data, headers, HttpStatus.OK);
    }

    private Mono<Void> renderResponseJson(ServerHttpResponse serverResponse, Object data, MultiValueMap<String, String> headers, HttpStatus status) {
        DataBufferFactory bufferFactory = serverResponse.bufferFactory();
        DataBuffer dataBuffer = bufferFactory.wrap(SerializationUtils.serialize(data));
        serverResponse.setStatusCode(status);
        serverResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if(headers != null) {
            serverResponse.getHeaders().addAll(headers);
        }
        return serverResponse.writeWith(Mono.just(dataBuffer));
    }

}
