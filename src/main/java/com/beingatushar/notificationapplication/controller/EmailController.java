package com.beingatushar.notificationapplication.controller;

import com.beingatushar.notificationapplication.model.EmailAttachment;
import com.beingatushar.notificationapplication.service.EmailNotificationService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class EmailController {

    private final EmailNotificationService emailService;

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> sendEmailWithAttachments(
            @RequestParam("recipient") @Email(message = "Invalid email format") String recipient,
            @RequestParam("subject") @NotBlank(message = "Subject cannot be blank") String subject,
            @RequestParam("body") @NotBlank(message = "Body cannot be blank") String body,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "fileNames", required = false) List<String> customNames) {

        log.info("Received email request. Recipient: '{}', Subject: '{}'", recipient, subject);

        try {
            List<EmailAttachment> attachments = mapToAttachments(files, customNames);

            // Fires asynchronously. Thread is released immediately!
            emailService.sendNotificationWithAttachments(recipient, subject, body, attachments);

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of("status", "Accepted", "message", "Email is processing in the background."));

        } catch (Exception e) {
            log.error("Error processing file upload for {}: {}", recipient, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "Error", "message", e.getMessage()));
        }
    }

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