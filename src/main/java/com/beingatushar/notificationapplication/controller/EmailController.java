package com.beingatushar.notificationapplication.controller;

import com.beingatushar.notificationapplication.model.EmailAttachment;
import com.beingatushar.notificationapplication.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class EmailController {

    private final EmailNotificationService emailService;

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttachments(
            @RequestParam(value = "recipient", required = true) String recipient,
            @RequestParam(value = "subject", required = true) String subject,
            @RequestParam(value = "body", required = true) String body,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "fileNames", required = false) List<String> customNames) {

        log.info("Received email request. Recipient: '{}', Subject: '{}'", recipient, subject);

        try {
            // 1. Delegate mapping to a dedicated method
            List<EmailAttachment> attachments = mapToAttachments(files, customNames);
            log.debug("Successfully mapped {} attachments for email to {}", attachments.size(), recipient);

            // 2. Call the service
            boolean success = emailService.sendNotificationWithAttachments(recipient, subject, body, attachments);

            if (success) {
                log.info("Email successfully dispatched to {}", recipient);
                return ResponseEntity.ok("Email sent successfully to " + recipient);
            } else {
                log.error("Email service returned false for recipient {}", recipient);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send email. Check server logs.");
            }

        } catch (Exception e) {
            // Note: Passing 'e' as the second argument ensures the full stack trace is logged
            log.error("Exception occurred while processing email request for {}: ", recipient, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    // --- Modular Helper Methods ---

    private List<EmailAttachment> mapToAttachments(List<MultipartFile> files, List<String> customNames) throws IOException {
        if (files == null || files.isEmpty()) {
            log.debug("No attachments provided in the request.");
            return List.of();
        }

        log.debug("Processing {} uploaded file(s).", files.size());
        List<EmailAttachment> attachments = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            if (file != null && !file.isEmpty()) {
                String finalName = resolveFileName(file, customNames, i);
                log.debug("Attachment {} mapped with filename: '{}' (Size: {} bytes)", i + 1, finalName, file.getSize());
                attachments.add(new EmailAttachment(finalName, file.getBytes(), file.getContentType()));
            } else {
                log.warn("Empty or null file encountered at index {}", i);
            }
        }

        return attachments;
    }

    private String resolveFileName(MultipartFile file, List<String> customNames, int index) {
        if (customNames != null && customNames.size() > index) {
            String customName = customNames.get(index);
            if (customName != null && !customName.isBlank()) {
                log.trace("Applying custom filename '{}' at index {}", customName.trim(), index);
                return customName.trim();
            }
        }

        log.trace("Applying original filename '{}' at index {}", file.getOriginalFilename(), index);
        return file.getOriginalFilename();
    }
}