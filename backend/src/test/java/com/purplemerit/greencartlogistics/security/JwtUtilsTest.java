package com.purplemerit.greencartlogistics.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtUtils
 */
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    
    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyForJWTTokenGeneration");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000); // 24 hours
    }

    @Test
    void testGenerateJwtToken() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn("testuser");

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGetUserNameFromJwtToken() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn("testuser");

        String token = jwtUtils.generateJwtToken(authentication);

        // When
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getUsername()).thenReturn("testuser");

        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateJwtToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateJwtToken_MalformedToken() {
        // Given
        String malformedToken = "malformed-token";

        // When
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateJwtToken_EmptyToken() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtUtils.validateJwtToken(emptyToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testValidateJwtToken_NullToken() {
        // Given
        String nullToken = null;

        // When
        boolean isValid = jwtUtils.validateJwtToken(nullToken);

        // Then
        assertFalse(isValid);
    }
}
