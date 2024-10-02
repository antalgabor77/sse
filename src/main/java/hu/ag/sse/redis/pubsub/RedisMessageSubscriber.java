package hu.ag.sse.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.ag.sse.model.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: {}", message.toString());
        try {
            Event orderEvent = objectMapper.readValue(message.getBody(), Event.class);
            applicationEventPublisher.publishEvent(orderEvent);
        }catch (Exception ex) {
            log.error("Error", ex);
        }
    }

}
