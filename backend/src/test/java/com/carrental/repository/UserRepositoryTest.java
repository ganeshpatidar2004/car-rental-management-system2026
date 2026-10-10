package com.carrental.repository;

import com.carrental.entity.Role;
import com.carrental.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User("Alice Brown", "alice@example.com", "$2a$10$hashedPasswordSample", Role.CUSTOMER);
        userRepository.save(user);
    }

    @Test
    @DisplayName("findByEmail - returns user when email exists")
    void findByEmail_ExistingEmail_ReturnsUser() {
        Optional<User> found = userRepository.findByEmail("alice@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Alice Brown");
        assertThat(found.get().getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    @DisplayName("findByEmail - returns empty when email does not exist")
    void findByEmail_NonExistentEmail_ReturnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nobody@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail - returns true when email exists")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("alice@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - returns false when email does not exist")
    void existsByEmail_NonExistentEmail_ReturnsFalse() {
        boolean exists = userRepository.existsByEmail("nobody@example.com");
        assertThat(exists).isFalse();
    }
}
