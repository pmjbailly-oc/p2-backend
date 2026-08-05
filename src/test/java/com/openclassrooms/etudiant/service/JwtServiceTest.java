package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private static final String SECRET = "MGQxYjFjYmIzNDcxNzA4MjgwMGUyNTEwYzM3ZWNkYmVhNzg2MTk2MDA5OThlYmZkZGM5NjRjZTIxNzA0NjhhZQ==";
    private static final long EXPIRATION = 3600000L;
    private static final String ISSUER = "etudiant-backend";
    private static final String LOGIN = "pmj.bailly";

    private JwtService jwtService;
    private User user;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "issuer", ISSUER);

        user = new User();
        user.setLogin(LOGIN);
        user.setPassword("password");
    }

    @Test
    public void test_generate_token_is_not_null_and_contains_subject() {
        // WHEN
        String token = jwtService.generateToken(user);

        // THEN
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(LOGIN);
    }

    @Test
    public void test_extract_username_returns_login() {
        // GIVEN
        String token = jwtService.generateToken(user);

        // WHEN
        String username = jwtService.extractUsername(token);

        // THEN
        assertThat(username).isEqualTo(LOGIN);
    }

    @Test
    public void test_is_token_valid_true_for_matching_user() {
        // GIVEN
        String token = jwtService.generateToken(user);

        // WHEN
        boolean valid = jwtService.isTokenValid(token, user);

        // THEN
        assertThat(valid).isTrue();
    }

    @Test
    public void test_is_token_valid_false_for_other_user() {
        // GIVEN
        String token = jwtService.generateToken(user);
        User otherUser = new User();
        otherUser.setLogin("autre.login");
        otherUser.setPassword("password");

        // WHEN
        boolean valid = jwtService.isTokenValid(token, otherUser);

        // THEN
        assertThat(valid).isFalse();
    }
}
