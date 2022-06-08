package com.tirmizee.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirmizee.exception.data.JWTExpiredException;
import com.tirmizee.exception.data.JWTSignatureException;
import com.tirmizee.exception.data.UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@AllArgsConstructor
@Component
@Order(-2)
public class GlobalWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper jacksonMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        log.info("error: {}",  ex.getClass().getSimpleName());

        ServerHttpResponse serverResponse = exchange.getResponse();

        if(ex instanceof LockedException){
            return renderResponseJson(serverResponse, "account is locked");
        }

        if(ex instanceof AccountExpiredException){
            return renderResponseJson(serverResponse, "account is expired");
        }

        if(ex instanceof JWTExpiredException) {
            return renderResponseJson(serverResponse, "JWT is expired");
        }

        if(ex instanceof JWTSignatureException) {
            return renderResponseJson(serverResponse, "JWT signature does not match");
        }

        if(ex instanceof UnauthorizedException) {
            return renderResponseJson(serverResponse, "unauthorized", HttpStatus.UNAUTHORIZED);
        }

        return renderResponseJson(serverResponse, ex.getMessage());
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
        try {

            byte[] jsonResponse = jacksonMapper.writeValueAsBytes(data);
            DataBufferFactory bufferFactory = serverResponse.bufferFactory();
            DataBuffer dataBuffer = bufferFactory.wrap(jsonResponse);
            serverResponse.setStatusCode(status);
            serverResponse.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

            if(headers != null) {
                serverResponse.getHeaders().addAll(headers);
            }

            return serverResponse.writeWith(Mono.just(dataBuffer));

        } catch (Exception e) {
            return serverResponse.setComplete();
        }

    }

}
