package hu.ag.sse.service;

import hu.ag.sse.mapper.EventMapper;
import hu.ag.sse.model.Event;
import hu.ag.sse.redis.dcache.RedisCacheService;
import hu.ag.sse.repository.EmitterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Primary
@AllArgsConstructor
@Slf4j
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final EventMapper eventMapper;
    private RedisCacheService redisTaskService;

    public boolean checkIfAvailableOnThisInstance(String registrationId) {
        if (emitterRepository.get(registrationId).isPresent()) {
            return true;
        }
        return false;
    }

    public void sendNotification(String registrationId, Event event) {
        if (event == null) {
            log.debug("No server event to send to device.");
            return;
        }
        doSendNotification(registrationId, event);
    }

    private void doSendNotification(String registrationId, Event event) {
        if (checkIfAvailableOnThisInstance(registrationId)) {
            emitterRepository.get(registrationId).ifPresentOrElse(sseEmitter -> {
                try {
                    log.info("Sending event: {} for member: {}", event, registrationId);
                    sseEmitter.send(eventMapper.toSseEventBuilder(event));
                } catch (IOException | IllegalStateException e) {
                    log.error("Error while sending event: {} for member: {} - exception: {}", event, registrationId, e);
                    emitterRepository.remove(registrationId);
                    redisTaskService.removeCacheEntry(registrationId);
                }
            }, () -> log.info("No emitter for member {}", registrationId));
        } else {
            log.error("No emmitter found in this server so remove it from the distributed cache: {}", registrationId);
            emitterRepository.remove(registrationId);
            redisTaskService.removeCacheEntry(registrationId);
        }
    }

}
