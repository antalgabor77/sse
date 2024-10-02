package hu.ag.sse.mapper;

import hu.ag.sse.model.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Calendar;

@Component
@Slf4j
@AllArgsConstructor
public class EventMapper {

    public SseEmitter.SseEventBuilder toSseEventBuilder(Event event) {
        return SseEmitter.event()
                .id(Calendar.getInstance().getTimeInMillis() + "")
                .name(event.getType().name())
                .data(event.getMessage());
    }
}