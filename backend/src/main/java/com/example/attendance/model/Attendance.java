package com.example.attendance.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document("attendance")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Attendance {
  @Id private String id;
  private String rfidId;
  private String userId;
  private String name;

  private LocalDate date;
  private LocalTime checkInTime;
  private LocalTime checkOutTime;

  private String status;   // Present/Late/etc.
  private String readerId; // gate-1/gate-2
}
