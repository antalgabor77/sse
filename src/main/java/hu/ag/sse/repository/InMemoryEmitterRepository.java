package hu.ag.sse.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryEmitterRepository implements EmitterRepository {

    private Map<String, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();

    @Override
    public void addOrReplaceEmitter(String registrationId, SseEmitter emitter) {
        userEmitterMap.put(registrationId, emitter);
    }

    @Override
    public void remove(String registrationId) {
        if (userEmitterMap != null && userEmitterMap.containsKey(registrationId)) {
            log.debug("Removing emitter for member: {}", registrationId);
            userEmitterMap.remove(registrationId);
        } else {
            log.debug("No emitter to remove for member: {}", registrationId);
        }
    }

    @Override
    public Optional<SseEmitter> get(String registrationId) {
        return Optional.ofNullable(userEmitterMap.get(registrationId));
    }

    @Override
    public List<SseEmitter> getAll() {
        return  userEmitterMap.values().stream().collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Set<String> getKeys() {
        return  userEmitterMap.keySet();
    }
}
