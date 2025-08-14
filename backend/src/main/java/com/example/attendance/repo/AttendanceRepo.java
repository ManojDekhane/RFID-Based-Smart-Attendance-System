package com.example.attendance.repo;

import com.example.attendance.model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepo extends MongoRepository<Attendance, String> {
  Optional<Attendance> findByRfidIdAndDate(String rfidId, LocalDate date);
  List<Attendance> findAllByDate(LocalDate date);
}
