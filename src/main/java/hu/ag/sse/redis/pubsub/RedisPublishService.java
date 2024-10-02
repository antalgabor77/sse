package hu.ag.sse.redis.pubsub;

import hu.ag.sse.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisPublishService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Long publish(String topicName, Event orderEvent){
        log.info("Sending message to topic: [{}] [{}]", topicName, orderEvent);
        return redisTemplate.convertAndSend(topicName, orderEvent);
    }

}
