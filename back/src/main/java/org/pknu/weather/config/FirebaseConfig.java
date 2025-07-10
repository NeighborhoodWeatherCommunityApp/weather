package org.pknu.weather.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${fcm.service-account}")
    private String serviceAccountKeyContent;

    @Value("${fcm.project-id}")
    private String projectId;

    @PostConstruct
    public void initializeFirebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(serviceAccountKeyContent.getBytes())))
                    .setProjectId(projectId)
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized.");
        } else {
            log.info("FirebaseApp already initialized. Skipping.");
        }
    }
}

