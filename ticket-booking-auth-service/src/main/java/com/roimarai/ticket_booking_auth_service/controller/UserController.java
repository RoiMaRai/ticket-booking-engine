package com.roimarai.ticket_booking_auth_service.controller;

import com.roimarai.ticket_booking_auth_service.model.User;
import com.roimarai.ticket_booking_auth_service.repository.AttendeeRepository;
import com.roimarai.ticket_booking_auth_service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttendeeRepository attendeeRepository;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, AttendeeRepository attendeeRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.attendeeRepository = attendeeRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user){
        String hashed = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashed);
        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
