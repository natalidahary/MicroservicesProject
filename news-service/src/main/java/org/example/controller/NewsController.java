package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NewsRequest;
import org.example.dto.NotificationRequest;
import org.example.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;


@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    private final NewsService newsService;

    @PostMapping("/processPreferences")
    public ResponseEntity<String> processPreferences(@RequestBody NewsRequest newsRequest) throws UnsupportedEncodingException {
        log.info("Received preferences: {}", newsRequest);
        newsService.processPreferences(newsRequest);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/sendNotification")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        newsService.sendNotification(notificationRequest);
        return ResponseEntity.ok("Notification sent to: " + notificationRequest.email());
    }

    @PostMapping("/invoke")
    public ResponseEntity<Void> invokeOtherService(@RequestParam String serviceId, @RequestParam String methodName, @RequestBody String request) {
        newsService.invokeOtherService(serviceId, methodName, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
