package com.example.attendance.web;

import com.example.attendance.model.Attendance;
import com.example.attendance.repo.AttendanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
@CrossOrigin(origins = {"http://localhost:5173"}) // dev: Vite
public class AttendanceController {

  private final AttendanceRepo attendance;

  @GetMapping("/today")
  public List<Attendance> today() {
    return attendance.findAllByDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
  }

  // Simple placeholder: extend with date range filters when needed
  @GetMapping("/user/{rfidId}")
  public List<Attendance> byUser(@PathVariable String rfidId) {
    return attendance.findAll().stream().filter(a -> rfidId.equals(a.getRfidId())).toList();
  }
}
