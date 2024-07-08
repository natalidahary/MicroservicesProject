package org.example.service;

import lombok.Value;
import org.example.dto.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

   // @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    public void sendNotification(NotificationRequest notificationRequest) {
        RestTemplate restTemplate = new RestTemplate();
        NotificationRequest emailRequest = new NotificationRequest(notificationRequest.email(), notificationRequest.subject(), notificationRequest.message());
        restTemplate.postForEntity(SENDGRID_API_URL, emailRequest, String.class);
    }
}
