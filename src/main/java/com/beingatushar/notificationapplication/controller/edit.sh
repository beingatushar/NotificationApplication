# 1. Update build.gradle to include H2 driver
sed -i "s/runtimeOnly 'org.postgresql:postgresql'/runtimeOnly 'com.h2database:h2'/g" build.gradle

# 2. Update application.yaml to use local H2 database
cat << 'EOF' > src/main/resources/application.yaml
spring:
  threads:
    virtual:
      enabled: true

  datasource:
    url: jdbc:h2:mem:notificationdb;DB_CLOSE_DELAY=-1
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.H2Dialect

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

# 3. Fix settings.gradle for Java 21 support
cat << 'EOF' > settings.gradle
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = 'NotificationApplication'
EOF

# 4. Update the email service to support emojis/HTML
sed -i 's/helper.setText(body);/helper.setText(body, true);/g' src/main/java/com/beingatushar/notificationapplication/service/impl/GmailNotificationService.java

# 5. Push to GitHub so your run.sh script pulls the correct version
git add build.gradle src/main/resources/application.yaml settings.gradle src/main/java/com/beingatushar/notificationapplication/service/impl/GmailNotificationService.java
git commit -m "fix: move to local H2 database and enable HTML email support"
#git push origin main

# 6. Execute the run script
./run.sh