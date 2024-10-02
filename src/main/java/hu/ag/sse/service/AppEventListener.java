package hu.ag.sse.service;

import hu.ag.sse.model.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class AppEventListener {

    private final NotificationService notificationService;
//    private RedisService redisService;
//    private RedisTaskService redisTaskService;

    @EventListener(ApplicationReadyEvent.class)
    public void applicationStartup() {
        log.info("applicationStartup");
    }


    @EventListener
    public void handleContextStart(Event event) {
        log.info("BalanceChangedEvent");
        notificationService.sendNotification(event.getRegistrationId(), event);
    }

}
