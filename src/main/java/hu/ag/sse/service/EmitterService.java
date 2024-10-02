package hu.ag.sse.service;

import hu.ag.sse.repository.EmitterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalTime;
import java.util.Calendar;

@Service
@Slf4j
public class EmitterService {

    private final long eventsTimeout;
    private final EmitterRepository repository;

    public EmitterService(@Value("${events.connection.timeout}") long eventsTimeout,
                          EmitterRepository repository) {
        this.eventsTimeout = eventsTimeout;
        this.repository = repository;
    }

    public SseEmitter createEmitter(String registrationId) {
        SseEmitter emitter = new SseEmitter(eventsTimeout);
        emitter.onCompletion(() -> repository.remove(registrationId));
        emitter.onTimeout(() -> repository.remove(registrationId));
        emitter.onError(e -> {
            log.error("Create SseEmitter exception", e);
            repository.remove(registrationId);
        });
        repository.addOrReplaceEmitter(registrationId, emitter);
        sentInit (registrationId);
        return emitter;
    }

    private void sentInit(String registrationId) {
        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data("created - " + LocalTime.now().toString())
                    .id(Calendar.getInstance().getTimeInMillis() + "")
                    .name("accepted");
            repository.get(registrationId).get().send(event);
        } catch (Exception ex) {
            log.error("Error", ex);
            repository.remove(registrationId);
        }
    }

}
