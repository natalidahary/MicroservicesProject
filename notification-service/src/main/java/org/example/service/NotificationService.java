package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
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
    private final DaprClient daprClient;
    private final ObjectMapper objectMapper;

    public void sendNotification(NotificationRequest notificationRequest) {
        log.info("Sending notification to: {}", notificationRequest.email());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(notificationRequest.email());
            helper.setSubject(notificationRequest.title());

            String emailContent = notificationRequest.content() + "<br><br>" + notificationRequest.link();
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Notification sent successfully to: {}", notificationRequest.email());

            // Publish an event to notify other services
            daprClient.publishEvent("rabbitmq-pubsub", "notification-sent", notificationRequest).block();
        } catch (MessagingException e) {
            log.error("Error sending notification to: {}", notificationRequest.email(), e);
        }
    }
}
