package com.beingatushar.notificationapplication.service.impl;

import com.beingatushar.notificationapplication.model.EmailAttachment;
import com.beingatushar.notificationapplication.service.EmailNotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailNotificationService implements EmailNotificationService {

    private final JavaMailSender mailSender;

    @Override
    public boolean sendNotification(String recipient, String subject, String body) {
        return sendNotificationWithAttachments(recipient, subject, body, List.of());
    }

    @Override
    public boolean sendNotificationWithAttachments(String recipient, String subject, String body, List<EmailAttachment> attachments) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // Set 'true' to indicate this is a multipart message (supports attachments)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body);

            // Dynamically add all modular attachments
            if (attachments != null) {
                for (EmailAttachment attachment : attachments) {
                    helper.addAttachment(
                            attachment.filename(),
                            new ByteArrayResource(attachment.data())
                    );
                }
            }

            mailSender.send(message);
            log.info("Email sent successfully to {} with {} attachments.", recipient, attachments != null ? attachments.size() : 0);
            return true;

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", recipient, e.getMessage());
            return false;
        }
    }
}