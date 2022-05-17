package com.tirmizee.repository;

import com.tirmizee.repository.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveSortingRepository<UserEntity, Integer> {

    Mono<UserEntity> findByUsername(String username);

}
