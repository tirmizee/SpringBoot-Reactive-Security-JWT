package com.tirmizee.controller.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ProfileController {

    @GetMapping("/profile")
    public Mono<ResponseEntity> profile() {
        return Mono.just(ResponseEntity.ok("hello"));
    }

}
