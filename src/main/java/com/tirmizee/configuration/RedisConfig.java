package com.tirmizee.configuration;

import com.tirmizee.service.model.RefreshTokenDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, RefreshTokenDetail> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        Jackson2JsonRedisSerializer<RefreshTokenDetail> valueSerializer =
                new Jackson2JsonRedisSerializer<>(RefreshTokenDetail.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, RefreshTokenDetail> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, RefreshTokenDetail> context = builder
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}
