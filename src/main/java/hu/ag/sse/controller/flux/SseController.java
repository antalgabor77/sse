package hu.ag.sse.controller.flux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;


@Slf4j
@RestController
public class SseController {

    @GetMapping("/sse-flux")
    public Flux<ServerSentEvent<Object>> streamEvents() {
        var normalEventFlux = Flux.interval(Duration.ofSeconds(1)).map(sequence -> createEvent(sequence)).log();
        var keepAliveEventFlux = Flux.interval(Duration.ofSeconds(10)).map(sequence -> createKeepAliveEvent(sequence)).log();
        return Flux.merge(normalEventFlux, keepAliveEventFlux);
    }

    private ServerSentEvent<Object> createEvent (Long sequence) {
        return ServerSentEvent.<Object> builder()
                .id(String.valueOf(sequence))
                .event("periodic-event")
                .data("SSE - " + LocalTime.now().toString())
                .comment("comment")
                .build();
    }
    private ServerSentEvent<Object> createKeepAliveEvent (Long sequence) {
        return ServerSentEvent.<Object> builder()
                .id(String.valueOf(sequence))
                .event("keep-alive")
                .data("")
                .build();
    }

}
