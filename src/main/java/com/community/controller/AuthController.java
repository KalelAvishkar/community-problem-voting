package com.community.controller;

import com.community.dto.SignupRequest;
import com.community.dto.LoginRequest;
import com.community.entity.Role;
import com.community.entity.User;
import com.community.repository.UserRepository;
import com.community.security.JwtUtil;
import com.community.service.EmailService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        if (request.getName() == null || request.getName().isBlank())
            return ResponseEntity.badRequest().body("Name required");

        if (request.getEmail() == null || request.getEmail().isBlank())
            return ResponseEntity.badRequest().body("Email required");

        if (request.getPassword() == null || request.getPassword().isBlank())
            return ResponseEntity.badRequest().body("Password required");

        if (userRepository.findByEmail(request.getEmail()).isPresent())
            return ResponseEntity.badRequest().body("Email already registered");

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER); // ✅ IMPORTANT

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Optional<User> dbUser = userRepository.findByEmail(request.getEmail());

        if (dbUser.isEmpty())
            return ResponseEntity.badRequest().body("Invalid email");

        if (!passwordEncoder.matches(request.getPassword(),
                dbUser.get().getPassword()))
            return ResponseEntity.badRequest().body("Invalid password");

        String token = jwtUtil.generateToken(
                dbUser.get().getEmail(),
                dbUser.get().getRole().name()
        );

        return ResponseEntity.ok(token);
    }
}