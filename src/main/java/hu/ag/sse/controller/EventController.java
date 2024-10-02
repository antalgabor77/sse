package hu.ag.sse.controller;

import hu.ag.sse.service.EmitterService;
import hu.ag.sse.redis.dcache.RedisCacheService;
import hu.ag.sse.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EmitterService emitterService;
    private final RedisCacheService redisCacheService;

    @Value("${server.instance.pubsub.topic}")
    private String pubSubTopicName;

    @GetMapping("/sse")
    public SseEmitter subscribeToEvents(@RequestParam("token") String token) {
        log.info("subscribeToEvents: [{}]", token);
        String registrationId = token;
        if (token.startsWith("ey")) {
            try {
                registrationId = JwtTokenUtils.extractSubject(token);
            } catch (io.jsonwebtoken.ExpiredJwtException expiredJwtException) {
                log.error("Expired JWT token", expiredJwtException);
                return null;
            }
        }
        log.info("Subscribing member with id [{}] to channel [{}]", registrationId, pubSubTopicName);
        redisCacheService.addConnectionInfo(registrationId, pubSubTopicName);
        return emitterService.createEmitter(registrationId);
    }

    @GetMapping("/sse/token")
    public Mono<String> generateToken(@RequestParam("registrationId") String registrationId) {
        log.info("generateToken");
        String token = JwtTokenUtils.generateToken(registrationId);
        log.info("generated token: [{}]", token);
        return Mono.just(token);
    }

}
