# 1. Update build.gradle to use PostgreSQL instead of MySQL
sed -i "s/runtimeOnly 'com.mysql:mysql-connector-j'/runtimeOnly 'org.postgresql:postgresql'/g" build.gradle

# 2. Update application.yaml for PostgreSQL and Environment Variables
cat << 'EOF' > src/main/resources/application.yaml
spring:
  threads:
    virtual:
      enabled: true

  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect

  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

  servlet:
    multipart:
      max-file-size: 25MB
      max-request-size: 50MB
EOF

# 3. Update settings.gradle to include the Foojay plugin for Java 21 support
cat << 'EOF' > settings.gradle
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = 'NotificationApplication'
EOF

# 4. Git Push changes
git add build.gradle src/main/resources/application.yaml settings.gradle
git commit -m "chore: migrate to hibernate postgresql and add foojay plugin"
git push origin main