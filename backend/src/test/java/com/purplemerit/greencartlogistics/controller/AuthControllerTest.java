package com.purplemerit.greencartlogistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.purplemerit.greencartlogistics.dto.LoginRequest;
import com.purplemerit.greencartlogistics.model.User;
import com.purplemerit.greencartlogistics.repository.UserRepository;
import com.purplemerit.greencartlogistics.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController
 */
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void testSignIn_Success() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("manager", "manager123");
        
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mock-jwt-token");
        
        // Mock UserPrincipal
        com.purplemerit.greencartlogistics.security.UserPrincipal userPrincipal = 
                mock(com.purplemerit.greencartlogistics.security.UserPrincipal.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getId()).thenReturn("user123");
        when(userPrincipal.getUsername()).thenReturn("manager");
        when(userPrincipal.getEmail()).thenReturn("manager@test.com");
        when(userPrincipal.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        User mockUser = new User();
        mockUser.setId("user123");
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("manager"));
    }

    @Test
    void testSignIn_InvalidCredentials() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("invalid", "invalid");
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testInitializeDefaultUser_Success() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("manager123")).thenReturn("encoded-password");
        
        User savedUser = new User();
        savedUser.setUsername("manager");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/api/auth/initialize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Default manager user created successfully!"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testInitializeDefaultUser_UsersAlreadyExist() throws Exception {
        // Given
        when(userRepository.count()).thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/auth/initialize"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Users already exist in the system!"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSignIn_MissingUsername() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("", "password");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignIn_MissingPassword() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("username", "");

        // When & Then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}
