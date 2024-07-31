package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationRequest;
import org.example.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @PostMapping("/notificationqueue")
    public ResponseEntity<Void> receiveNotification(@RequestBody byte[] payload) {
        try {
            NotificationRequest notificationRequest = objectMapper.readValue(payload, NotificationRequest.class);
            log.info("Received notification request from Dapr: {}", notificationRequest);
            notificationService.sendNotification(notificationRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing notification message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}