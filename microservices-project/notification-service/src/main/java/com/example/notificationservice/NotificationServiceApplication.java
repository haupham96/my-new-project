package com.example.notificationservice;

import lombok.extern.slf4j.Slf4j;
import com.example.notificationservice.dto.OrderPlacedEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notificationTopic")
    public void handlerNotification(OrderPlacedEvent orderPlacedEvent){
        /* send out an email notification */
        log.info("recieved notification for Order - {} ",orderPlacedEvent.getOrderNumber());
    }

}