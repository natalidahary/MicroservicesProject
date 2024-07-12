package org.example.service;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.NotificationRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {


    private final JavaMailSender mailSender;
    private final DaprClient daprClient = new DaprClientBuilder().build();

    public void sendNotification(NotificationRequest notificationRequest) {
        log.info("Sending notification to: {}", notificationRequest.email());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(notificationRequest.email());
            helper.setSubject(notificationRequest.subject());
            helper.setText(notificationRequest.message(), true);
            mailSender.send(message);
            log.info("Notification sent successfully to: {}", notificationRequest.email());

            // Publish an event to notify other services
            daprClient.publishEvent("pubsub", "notification-sent", notificationRequest).block();
        } catch (MessagingException e) {
            log.error("Error sending notification to: {}", notificationRequest.email(), e);
        }
    }
}
