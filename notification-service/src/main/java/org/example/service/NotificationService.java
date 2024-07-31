package org.example.service;

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

    public void sendNotification(NotificationRequest notificationRequest) {
        log.info("Received notification request: {}", notificationRequest);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(notificationRequest.email());
            helper.setSubject(notificationRequest.title());

            String emailContent = notificationRequest.content() + "<br><br>" + notificationRequest.link();
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Notification sent successfully to: {}", notificationRequest.email());
        } catch (MessagingException e) {
            log.error("Error sending notification to: {}", notificationRequest.email(), e);
        }
    }
}
