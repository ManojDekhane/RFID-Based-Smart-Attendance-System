package com.example.attendance.repo;

import com.example.attendance.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
  Optional<User> findByRfidId(String rfidId);
}
