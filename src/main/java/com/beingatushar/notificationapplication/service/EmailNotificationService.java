package com.beingatushar.notificationapplication.service;


import com.beingatushar.notificationapplication.model.EmailAttachment;

import java.util.List;

public interface EmailNotificationService extends NotificationService {

    // For simple text emails
    void sendNotification(String recipient, String subject, String body);

    // Overloaded method for modular attachments
    void sendNotificationWithAttachments(
            String recipient,
            String subject,
            String body,
            List<EmailAttachment> attachments
    );
}