package com.beingatushar.notificationapplication.repository;

import com.beingatushar.notificationapplication.model.EmailAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuditLogRepository extends JpaRepository<EmailAuditLog, Long> {
}