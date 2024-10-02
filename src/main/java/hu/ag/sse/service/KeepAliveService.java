package hu.ag.sse.service;

import hu.ag.sse.redis.dcache.RedisCacheService;
import hu.ag.sse.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeepAliveService {

    @Value("${server.instance.pubsub.topic}")
    private String pubSubTopicName;

    private final EmitterRepository repository;

    private final RedisCacheService redisCacheService;

    @Scheduled(fixedDelay = 30000)
    public void sendKeepAlive() {
        log.info("Send keep-alive for established connections [{}]", repository.getKeys().stream().count());
        repository.getKeys().stream().forEach(registrationId -> {
            SseEmitter sseEmitter = repository.get(registrationId).orElse(null);
            if (sseEmitter != null) {
                try {
                    sseEmitter.send(createKeepAliveMessage());
                    try {
                        redisCacheService.addConnectionInfo(registrationId, pubSubTopicName);
                    } catch (Exception ex) {
                        log.error("Error updating connection info", ex);
                    }
                } catch (IOException e) {
                    sseEmitter.complete();
                    try {
                        redisCacheService.removeCacheEntry(registrationId);
                    } catch (Exception ex) {
                        log.error("Error removing disconnected connection info");
                    }
                }
            }
        });
    }

    private SseEmitter.SseEventBuilder createKeepAliveMessage () {
        return SseEmitter.event()
                .data("")
                .id(Calendar.getInstance().getTimeInMillis() + "")
                .name("keep-alive");
    }

}
