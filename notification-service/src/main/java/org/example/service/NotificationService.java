package org.example.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    /*
   // @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    public void sendNotification(NotificationRequest notificationRequest) {
        RestTemplate restTemplate = new RestTemplate();
        NotificationRequest emailRequest = new NotificationRequest(notificationRequest.email(), notificationRequest.subject(), notificationRequest.message());
        restTemplate.postForEntity(SENDGRID_API_URL, emailRequest, String.class);
    }*/

    private final JavaMailSender mailSender;

    public void sendNotification(NotificationRequest notificationRequest) {
        log.info("Sending notification to: {}", notificationRequest.email());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(notificationRequest.email());
            helper.setSubject("Your Daily News Update");
            helper.setText(notificationRequest.summary(), true);
            mailSender.send(message);
            log.info("Notification sent successfully to: {}", notificationRequest.email());
        } catch (MessagingException e) {
            log.error("Error sending notification to: {}", notificationRequest.email(), e);
        }
    }
}
