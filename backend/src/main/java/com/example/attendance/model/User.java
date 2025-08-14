package com.example.attendance.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
  @Id private String id;
  private String rfidId;
  private String name;
  private String role;       // admin/employee
  private String department; // optional
  private String email;      // optional
}
