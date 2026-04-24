package com.beingatushar.notificationapplication.service.impl;

import com.beingatushar.notificationapplication.model.EmailAttachment;
import com.beingatushar.notificationapplication.model.EmailAuditLog;
import com.beingatushar.notificationapplication.repository.EmailAuditLogRepository;
import com.beingatushar.notificationapplication.service.EmailNotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailNotificationService implements EmailNotificationService {

    private final JavaMailSender mailSender;
    private final EmailAuditLogRepository auditLogRepository;

    @Override
    public void sendNotification(String recipient, String subject, String body) {
        // Updated to return void to match the async interface
        sendNotificationWithAttachments(recipient, subject, body, List.of());
    }

    @Async // Runs on a Java 21 Virtual Thread if enabled in application.yaml
    @Retryable(value = MailException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    @Override
    public void sendNotificationWithAttachments(String recipient, String subject, String body, List<EmailAttachment> attachments) {

        // 1. Create Initial Audit Log as PENDING
        EmailAuditLog audit = EmailAuditLog.builder()
                .recipient(recipient)
                .subject(subject)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        audit = auditLogRepository.save(audit);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body,true);

            for (EmailAttachment attachment : attachments) {
                helper.addAttachment(attachment.filename(), new ByteArrayResource(attachment.data()));
            }

            mailSender.send(message);

            // 2. Mark as SENT on success
            audit.setStatus("SENT");
            audit.setCompletedAt(LocalDateTime.now());
            auditLogRepository.save(audit);

            log.info("Email sent to {}. Audit ID: {}", recipient, audit.getId());
        } catch (Exception e) {
            log.warn("Attempt failed for email to {}. Retrying if eligible...", recipient);
            // Re-throwing allows @Retryable to trigger the next attempt
            throw new RuntimeException(e);
        }
    }

    // 3. Fallback Method: Must return void to match the main method
    @Recover
    public void recover(RuntimeException e, String recipient, String subject, String body, List<EmailAttachment> attachments) {
        log.error("All retries exhausted for email to {}", recipient);

        // Final status update to FAILED in MySQL/H2
        EmailAuditLog audit = EmailAuditLog.builder()
                .recipient(recipient)
                .subject(subject)
                .status("FAILED")
                .errorMessage(e.getMessage())
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(audit);
    }
}