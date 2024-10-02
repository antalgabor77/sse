package hu.ag.sse.controller;

import hu.ag.sse.model.Event;
import hu.ag.sse.model.EventType;
import hu.ag.sse.redis.pubsub.RedisPublishService;
import hu.ag.sse.redis.dcache.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequestMapping("/admin")
@RestController
public class AdminController {

    @Autowired
    private RedisPublishService redisService;

    @Autowired
    private RedisCacheService redisTaskService;

    @GetMapping("/balance/{registrationId}/{amount}")
    public Mono<String> balance(@PathVariable("registrationId") String registrationId, @PathVariable("amount") Long amount) {
        log.info("ADMIN: New balance arrived [{}] [{}]", registrationId, amount);
        Event event = new Event(EventType.BALANCE, registrationId,""+amount);
        String topicName = convert(redisTaskService.getCacheValue(registrationId));
        log.info("ADMIN: topicName based on distributed cache: [{}]", topicName);
        if (topicName == null) {
            return Mono.just(("No topic found for ["+registrationId+"]"));
        }
        redisService.publish(topicName, event);
        return Mono.just(("Message ["+registrationId+"]["+amount+"] published to topic ["+topicName+"]"));
    }

    @GetMapping("/cache/{registrationId}")
    public Mono<String> status(@PathVariable("registrationId") String registrationId) {
        log.info("ADMIN: cache [{}]", registrationId);
        String value = convert(redisTaskService.getCacheValue(registrationId));
        log.info("ADMIN: cache key value [{}] [{}]",registrationId, value);
        return Mono.just(("Cache content: key: ["+registrationId+"], value: [" + value + "]"));
    }

    private String convert(Object obj) {
        if (obj == null) {
            return null;
        }
        String value = null;
        if (obj instanceof SimpleValueWrapper) {
            SimpleValueWrapper svw = (SimpleValueWrapper) obj;
            value = svw.get().toString();
        } else {
            value = obj.toString();
        }
        return value;
    }

}
