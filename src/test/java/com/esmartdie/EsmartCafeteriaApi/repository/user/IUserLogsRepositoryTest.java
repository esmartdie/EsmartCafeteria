package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IUserLogsRepositoryTest {

    @Autowired
    private IUserLogsRepository userLogsRepository;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John");
        user.setLastName("Wick");
        user.setEmail("johnW@theboogieman.com");
        user.setPassword("password");
        user.setActive(true);
        userRepository.save(user);

        UserLogs userLogs = new UserLogs();
        userLogs.setUser(user);
        userLogs.setSessionStartDate(LocalDate.now());
        userLogs.setSessionEndTime(null);
        userLogsRepository.save(userLogs);
    }

    @AfterEach
    void tearDown() {
        userLogsRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindLastUserSession() {
        User user = userRepository.findByEmail("johnW@theboogieman.com").orElse(null);
        assertNotNull(user);

        List<UserLogs> lastUserSession = userLogsRepository.findLastUserSession(user);

        assertNotNull(lastUserSession);
        assertEquals(user, lastUserSession.get(0).getUser());
        assertNull(lastUserSession.get(0).getSessionEndDate());
    }
}