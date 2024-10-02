package hu.ag.sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmitterRepository {

    void addOrReplaceEmitter(String registrationId, SseEmitter emitter);

    void remove(String registrationId);

    Optional<SseEmitter> get(String registrationId);

    List<SseEmitter> getAll();

    Set<String> getKeys();

}
