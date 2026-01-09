package com.cityfix.citifix.infrastructure.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60 * 60);
    }

    @Test
    @DisplayName("Should generate a valid token for a user")
    void shouldGenerateToken() {
        UserDetails user = new User("alex@cityfix.com", "pass", Collections.emptyList());

        String token = jwtService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Should extract correct username from token")
    void shouldExtractUsername() {
        UserDetails user = new User("alex@cityfix.com", "pass", Collections.emptyList());
        String token = jwtService.generateToken(user);

        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo("alex@cityfix.com");
    }

    @Test
    @DisplayName("Should validate token correctly")
    void shouldValidateToken() {
        UserDetails user = new User("alex@cityfix.com", "pass", Collections.emptyList());
        String token = jwtService.generateToken(user);

        boolean isValid = jwtService.isTokenValid(token, user);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token for wrong user")
    void shouldInvalidateTokenForWrongUser() {
        UserDetails alex = new User("alex@cityfix.com", "pass", Collections.emptyList());
        UserDetails maria = new User("maria@cityfix.com", "pass", Collections.emptyList());

        String tokenForAlex = jwtService.generateToken(alex);

        boolean isValid = jwtService.isTokenValid(tokenForAlex, maria);

        assertThat(isValid).isFalse();
    }
}