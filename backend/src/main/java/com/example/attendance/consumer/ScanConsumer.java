package com.example.attendance.consumer;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.User;
import com.example.attendance.repo.AttendanceRepo;
import com.example.attendance.repo.UserRepo;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScanConsumer {

  private final FirebaseDatabase db;
  private final UserRepo users;
  private final AttendanceRepo attendance;

  @PostConstruct
  public void start() {
    db.getReference("scanQueue").addChildEventListener(new ChildEventListener() {
      @Override public void onChildAdded(DataSnapshot snap, String prevChildKey) { processOne(snap); }
      @Override public void onChildChanged(DataSnapshot s, String p) {}
      @Override public void onChildRemoved(DataSnapshot s) {}
      @Override public void onChildMoved(DataSnapshot s, String p) {}
      @Override public void onCancelled(DatabaseError error) {
        System.err.println("Firebase listener cancelled: " + error.getMessage());
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void processOne(DataSnapshot snap) {
    try {
      Map<String,Object> m = (Map<String,Object>) snap.getValue();
      if (m == null) { remove(snap); return; }

      String cardId = String.valueOf(m.get("cardId"));
      String readerId = String.valueOf(m.getOrDefault("readerId", "unknown"));
      long ts = Long.parseLong(String.valueOf(m.getOrDefault("ts", System.currentTimeMillis())));

      Optional<User> userOpt = users.findByRfidId(cardId);
      if (userOpt.isEmpty()) {
        // Unknown card -> deadletter
        db.getReference("deadletter").push().setValueAsync(m);
        remove(snap);
        return;
      }
      User user = userOpt.get();

      ZonedDateTime zdt = Instant.ofEpochMilli(ts).atZone(ZoneId.of("Asia/Kolkata"));
      LocalDate date = zdt.toLocalDate();
      LocalTime time = zdt.toLocalTime();

      Attendance rec = attendance.findByRfidIdAndDate(user.getRfidId(), date).orElse(null);
      if (rec == null) {
        rec = Attendance.builder()
            .rfidId(user.getRfidId())
            .userId(user.getId())
            .name(user.getName())
            .date(date)
            .checkInTime(time)
            .status("Present")
            .readerId(readerId)
            .build();
      } else {
        if (rec.getCheckOutTime() == null || time.isAfter(rec.getCheckOutTime())) {
          rec.setCheckOutTime(time);
        }
      }
      attendance.save(rec);

      Map<String,Object> live = new HashMap<>();
      live.put("name", user.getName());
      live.put("lastEvent", (rec.getCheckOutTime() == null) ? "CHECK_IN" : "CHECK_OUT");
      live.put("time", zdt.toString());
      db.getReference("live/presence/" + user.getRfidId()).setValueAsync(live);

      remove(snap);
    } catch (Exception e) {
      e.printStackTrace();
      db.getReference("deadletter").push().setValueAsync(snap.getValue());
      remove(snap);
    }
  }

  private void remove(DataSnapshot snap) {
    db.getReference("scanQueue").child(snap.getKey()).removeValueAsync();
  }
}
