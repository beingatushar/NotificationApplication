# 📧 Notification Application

A robust, modular Spring Boot 4 REST API built on Java 21 for sending automated email notifications. This service
supports complex text payloads, dynamic multiple file attachments, and custom file renaming on the fly.

It uses **Gmail SMTP** for dispatching emails, **MySQL** for persistence, and **Log4j2** for enterprise-grade file
rolling and console logging.

## 🛠 Prerequisites

* **Java:** Version 21 or higher
* **Database:** MySQL Server (Running locally on port 3306)
* **Build Tool:** Gradle

## 🔐 Environment Variables

To ensure your credentials remain secure and never get pushed to version control, this application requires the
following environment variables to be set on your host machine or in your IDE's Run Configuration before starting:

| Variable Name   | Description                                        | Example                   |
|:----------------|:---------------------------------------------------|:--------------------------|
| `DB_USERNAME`   | Your MySQL database username.                      | `root`                    |
| `DB_PASSWORD`   | Your MySQL database password.                      | `password123`             |
| `MAIL_USERNAME` | The Gmail address sending the notifications.       | `your-email@gmail.com`    |
| `MAIL_PASSWORD` | Your 16-digit Google App Password.                 | `abcd efgh ijkl mnop`     |
| `LOG_DIR`       | Absolute path where Log4j2 should write log files. | `/home/beingatushar/logs` |

## ⚙️ Application Configuration (`application.yml`)

The application is pre-configured to handle standard requests, but you should explicitly increase the default Spring
Boot file upload limits to allow larger attachments (like heavy PDFs).

Add this to your `src/main/resources/application.yml`:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 25MB      # Max size for a single attachment
      max-request-size: 50MB   # Max size for the entire HTTP request