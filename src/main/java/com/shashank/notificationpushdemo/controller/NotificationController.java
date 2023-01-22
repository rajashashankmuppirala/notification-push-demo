package com.shashank.notificationpushdemo.controller;

import com.shashank.notificationpushdemo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


   // ideal use case to get the userId from auth session
   @GetMapping("/subscribe")
   public SseEmitter subscribe(String userId){
      return notificationService.subscribe("test");
   }

    // ideal use case to get the userId from auth session
   @DeleteMapping("/unsubscribe")
   public ResponseEntity<?> unsubscribe(String userId){
        notificationService.unSubscribe("test");
        return ResponseEntity.ok().build();
    }

}
