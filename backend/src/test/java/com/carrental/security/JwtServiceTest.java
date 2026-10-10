package com.carrental.security;

import com.carrental.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private final String validSecret = "test_jwt_secret_key_minimum_32_characters_long_for_unit_tests!";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", validSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L); // 1 hour
        jwtService.init();
    }

    @Test
    @DisplayName("generateToken and validate - creates valid token and extracts subject and role")
    void generateToken_ExtractsClaimsSuccessfully() {
        String token = jwtService.generateToken("user@example.com", Role.CUSTOMER);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("user@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("CUSTOMER");

        UserDetails userDetails = new User(
                "user@example.com",
                "hashed",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid - returns false when token username does not match")
    void isTokenValid_MismatchedUsername_ReturnsFalse() {
        String token = jwtService.generateToken("user@example.com", Role.CUSTOMER);

        UserDetails differentUser = new User(
                "other@example.com",
                "hashed",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        assertThat(jwtService.isTokenValid(token, differentUser)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid - returns false for expired token")
    void isTokenValid_ExpiredToken_ReturnsFalse() {
        JwtService shortLivedService = new JwtService();
        ReflectionTestUtils.setField(shortLivedService, "secret", validSecret);
        ReflectionTestUtils.setField(shortLivedService, "jwtExpirationMs", -1000L); // Already expired
        shortLivedService.init();

        String expiredToken = shortLivedService.generateToken("expired@example.com", Role.CUSTOMER);

        UserDetails userDetails = new User(
                "expired@example.com",
                "hashed",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        assertThat(shortLivedService.isTokenValid(expiredToken, userDetails)).isFalse();
    }

    @Test
    @DisplayName("init - fails safely when secret is missing or too short")
    void init_InvalidSecret_ThrowsIllegalStateException() {
        JwtService missingSecretService = new JwtService();
        ReflectionTestUtils.setField(missingSecretService, "secret", "");
        assertThatThrownBy(missingSecretService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not configured");

        JwtService shortSecretService = new JwtService();
        ReflectionTestUtils.setField(shortSecretService, "secret", "short-secret");
        assertThatThrownBy(shortSecretService::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 256 bits");
    }
}
