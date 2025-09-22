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
        
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
    
    @PostMapping("/initialize")
    @Operation(summary = "Initialize default user", description = "Create default manager user if no users exist")
    public ResponseEntity<?> initializeDefaultUser() {
        if (userRepository.count() == 0) {
            // Create default manager user
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@greencart.com");
            manager.setPassword(encoder.encode("manager123"));
            
            Set<User.Role> roles = new HashSet<>();
            roles.add(User.Role.MANAGER);
            manager.setRoles(roles);
            
            userRepository.save(manager);
            
            return ResponseEntity.ok(new MessageResponse("Default manager user created successfully!"));
        }
        
        return ResponseEntity.badRequest()
                .body(new MessageResponse("Users already exist in the system!"));
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
