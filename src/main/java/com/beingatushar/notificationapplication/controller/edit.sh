# 1. Update build.gradle to use the H2 driver
sed -i "s/runtimeOnly 'org.postgresql:postgresql'/runtimeOnly 'com.h2database:h2'/g" build.gradle

# 2. Update application.yaml to use the local H2 database
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

# 3. Commit and Push to GitHub
git add build.gradle src/main/resources/application.yaml
git commit -m "fix: switch to local H2 database to bypass IPv6 networking blocks"
#git push origin main