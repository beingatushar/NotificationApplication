package com.beingatushar.notificationapplication.model;

/**
 * Represents an email attachment in a modular way.
 * * @param filename    The name of the file (e.g., "report.pdf")
 *
 * @param data        The actual file content in bytes
 * @param contentType The MIME type (e.g., "application/pdf", "text/csv")
 */
public record EmailAttachment(
        String filename,
        byte[] data,
        String contentType
) {
}