package com.beingatushar.notificationapplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;
    private String subject;
    private String status; // PENDING, SENT, FAILED

    @Column(length = 1000)
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}