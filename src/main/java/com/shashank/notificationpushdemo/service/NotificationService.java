package com.shashank.notificationpushdemo.service;

import com.shashank.notificationpushdemo.dto.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationService {

    private Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId){
        log.debug("Subscribing client for -->" + userId);
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitter.onCompletion(() -> emitters.remove(userId));
        sseEmitter.onError((error) -> emitters.remove(userId));
        sseEmitter.onTimeout(() -> emitters.remove(userId));
        emitters.put(userId, sseEmitter);
        return sseEmitter;
    }

    public void unSubscribe(String userId){
        log.debug("Unsubscribing client for -->" + userId);
        Optional.ofNullable(emitters.get(userId)).ifPresent(SseEmitter::complete);
    }

    public void publishUserSpecificNotification(String userId, Notification notification){
        Optional.ofNullable(emitters.get(userId)).ifPresent(sseEmitter -> {
            try {
                sseEmitter.send(notification);
            } catch (IOException e) {
                log.error("error -->" + e.getMessage());
                emitters.remove(userId);
            }
        });
    }

    public void publishNotificationsToAll(Notification notification){
        emitters.forEach((userId,sseEmitter) -> {
            try {
                sseEmitter.send(notification);
            } catch (IOException e) {
                log.error("error -->" + e.getMessage());
                emitters.remove(userId);
            }
        });
    }


}
