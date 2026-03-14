package com.beingatushar.notificationapplication.service;


import com.beingatushar.notificationapplication.model.EmailAttachment;

import java.util.List;

public interface EmailNotificationService extends NotificationService {

    // For simple text emails
    boolean sendNotification(String recipient, String subject, String body);

    // Overloaded method for modular attachments
    boolean sendNotificationWithAttachments(
            String recipient,
            String subject,
            String body,
            List<EmailAttachment> attachments
    );
}