package com.purplemerit.greencartlogistics.controller;

import com.purplemerit.greencartlogistics.dto.JwtResponse;
import com.purplemerit.greencartlogistics.dto.LoginRequest;
import com.purplemerit.greencartlogistics.dto.MessageResponse;
import com.purplemerit.greencartlogistics.model.User;
import com.purplemerit.greencartlogistics.repository.UserRepository;
import com.purplemerit.greencartlogistics.security.JwtUtils;
import com.purplemerit.greencartlogistics.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authentication controller for login and registration
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    @Operation(summary = "Sign in user", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt for user: " + loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Update last login time
            User user = userRepository.findById(userDetails.getId()).orElse(null);
            if (user != null) {
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
            }

            JwtResponse response = new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);

            System.out.println("Login successful for user: " + loginRequest.getUsername() + ", token generated: " + (jwt != null && jwt.length() > 0));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Login failed for user: " + loginRequest.getUsername() + ", error: " + e.getMessage());
            return ResponseEntity.status(401)
                    .body(new MessageResponse("Invalid username or password: " + e.getMessage()));
        }
    }

    @PostMapping("/initialize")
    @Operation(summary = "Initialize default users", description = "Create default manager and admin users if no users exist")
    public ResponseEntity<?> initializeDefaultUser() {
        try {
            long userCount = userRepository.count();

            if (userCount == 0) {
                // Create default manager user
                User manager = new User();
                manager.setUsername("manager");
                manager.setEmail("manager@greencart.com");
                manager.setPassword(encoder.encode("manager123"));

                Set<User.Role> managerRoles = new HashSet<>();
                managerRoles.add(User.Role.MANAGER);
                manager.setRoles(managerRoles);

                userRepository.save(manager);

                // Create default admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@greencart.com");
                admin.setPassword(encoder.encode("admin123"));

                Set<User.Role> adminRoles = new HashSet<>();
                adminRoles.add(User.Role.ADMIN);
                admin.setRoles(adminRoles);

                userRepository.save(admin);

                return ResponseEntity.ok(new MessageResponse("Default users created successfully"));
            }

            return ResponseEntity.ok(new MessageResponse("Users already exist in the system"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Error creating users: " + e.getMessage()));
        }
    }

    @GetMapping("/debug")
    @Operation(summary = "Debug endpoint", description = "Check user count and database connection")
    public ResponseEntity<?> debugInfo() {
        try {
            long userCount = userRepository.count();

            // Get all usernames for debugging
            List<String> usernames = userRepository.findAll().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            String usernameList = String.join(", ", usernames);

            return ResponseEntity.ok(new MessageResponse(
                    "Database connected. User count: " + userCount +
                            ". Users: [" + usernameList + "]"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Database error: " + e.getMessage()));
        }
    }

    @PostMapping("/test-user")
    @Operation(summary = "Test user credentials", description = "Check if user exists and password matches")
    public ResponseEntity<?> testUser(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

            if (userOpt.isEmpty()) {
                return ResponseEntity.ok(new MessageResponse("User '" + loginRequest.getUsername() + "' not found"));
            }

            User user = userOpt.get();
            boolean passwordMatches = encoder.matches(loginRequest.getPassword(), user.getPassword());

            String message = String.format(
                    "User: %s, Email: %s, Active: %s, Roles: %s, Password matches: %s",
                    user.getUsername(),
                    user.getEmail(),
                    user.isActive(),
                    user.getRoles(),
                    passwordMatches
            );

            return ResponseEntity.ok(new MessageResponse(message));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Error testing user: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-users")
    @Operation(summary = "Reset all users", description = "Delete all users and recreate defaults - USE WITH CAUTION")
    public ResponseEntity<?> resetUsers() {
        try {
            // Delete all users
            userRepository.deleteAll();

            // Create default manager user
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@greencart.com");
            manager.setPassword(encoder.encode("manager123"));

            Set<User.Role> managerRoles = new HashSet<>();
            managerRoles.add(User.Role.MANAGER);
            manager.setRoles(managerRoles);

            userRepository.save(manager);

            // Create default admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@greencart.com");
            admin.setPassword(encoder.encode("admin123"));

            Set<User.Role> adminRoles = new HashSet<>();
            adminRoles.add(User.Role.ADMIN);
            admin.setRoles(adminRoles);

            userRepository.save(admin);

            return ResponseEntity.ok(new MessageResponse("Users reset successfully. Created: manager, admin"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Error resetting users: " + e.getMessage()));
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token and return user info")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse("",
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        }

        return ResponseEntity.badRequest()
                .body(new MessageResponse("Invalid token!"));
    }
}
