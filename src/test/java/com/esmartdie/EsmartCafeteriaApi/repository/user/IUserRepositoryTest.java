package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IUserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;
    private User user;


    @BeforeEach
    void setUp() {
        user = new User(null, "John", "Wick", "john@theboogieman.com", "password", true, null);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByName_UserFound() {
        User foundUser = userRepository.findByName("John");
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getName());
    }

    @Test
    void testFindByName_UserNotFound() {
        User foundUser = userRepository.findByName("nonexistent");
        assertNull(foundUser);
    }

    @Test
    void testFindByEmail_UserFound() {
        Optional<User> foundUserOptional = userRepository.findByEmail("john@theboogieman.com");
        assertTrue(foundUserOptional.isPresent());
        assertEquals("john@theboogieman.com", foundUserOptional.get().getEmail());
    }

    @Test
    void testFindByEmail_UserNotFound() {
        Optional<User> foundUserOptional = userRepository.findByEmail("nonexistent@example.com");
        assertTrue(foundUserOptional.isEmpty());
    }

    @Test
    void testFindById_UserFound() {
        Optional<User> foundUserOptional = userRepository.findById(user.getId());
        assertTrue(foundUserOptional.isPresent());
        assertEquals(user.getId(), foundUserOptional.get().getId());
    }

    @Test
    void testFindById_UserNotFound() {
        Optional<User> foundUserOptional = userRepository.findById(-1L); // Non-existent ID
        assertTrue(foundUserOptional.isEmpty());
    }

}