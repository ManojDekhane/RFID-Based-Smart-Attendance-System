package com.example.attendance.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

  @Value("${firebase.databaseUrl}")
  private String databaseUrl;

  @Value("${firebase.serviceAccount}")
  private Resource serviceAccount;

  @Bean
  public FirebaseDatabase firebaseDatabase() throws IOException {
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
        .setDatabaseUrl(databaseUrl)
        .build();
    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(options);
    }
    return FirebaseDatabase.getInstance();
  }
}
