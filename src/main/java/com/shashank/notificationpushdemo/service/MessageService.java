package com.shashank.notificationpushdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashank.notificationpushdemo.dto.Notification;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Date;

@Service
@Slf4j
public class MessageService {

    final JmsTemplate jmsTemplate;
    final NotificationService notificationService;
    final ObjectMapper objectMapper;

    public MessageService(JmsTemplate jmsTemplate, NotificationService notificationService, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }



    @JmsListener(destination = "tasks", concurrency = "2")
    public void receiveMessage(Message msg) throws InterruptedException {
        log.debug( " received on " + Thread.currentThread().getName());

        try {
            String message = ((ActiveMQTextMessage) msg).getText();
            Notification notification = objectMapper.readValue(message, Notification.class);

           notificationService.publishUserSpecificNotification("test", notification);
           notificationService.publishNotificationsToAll(notification);

        } catch (JMSException e) {
           log.error("error-->"+ e.getMessage());
        } catch (JsonMappingException e) {
            log.error("error-->"+ e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("error-->"+ e.getMessage());
        }

    }

    @Scheduled(cron = "0/30 * * * * *")
    private void testMessagePush(){
       Date date = new Date();
       log.info("Sending a message.");
        jmsTemplate.convertAndSend("tasks", Notification.builder().id(date.getTime()).message("Hello at " + new Date().toString()).build());

    }

}
