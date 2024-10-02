package hu.ag.sse.kaffka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// https://www.stackchief.com/blog/Spring%20Boot%20Kafka%20Consumer
@Component
public class KaffkaConsumer {

    //@KafkaListener(topics = "test", concurrency = "2", groupId = "myGroup")
    public void processMessage(String content) {
        System.out.println("Message received: " + content);
    }

}
