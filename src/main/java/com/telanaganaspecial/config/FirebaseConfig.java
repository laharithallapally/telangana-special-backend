package com.telanaganaspecial.config;
import org.springframework.beans.factory.annotation.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FirebaseConfig {
    @Value("${firebase.service-account-path}")
    private String serviceAccountPath;

    @PostConstruct
    public void init() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return; // already initialized (e.g. in tests)
        }
        try (InputStream serviceAccount = new FileInputStream(serviceAccountPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

}
