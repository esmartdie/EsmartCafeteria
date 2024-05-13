package com.esmartdie.EsmartCafeteriaApi.model.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserLogsTest {

    @Test
    void testConstructorAndGetters() {
        Long id = 1L;
        User user = new User();
        LocalDate sessionStart = LocalDate.of(2024, 4, 28);
        LocalTime sessionTimeStart = LocalTime.now();
        LocalTime sessionEndTime = LocalTime.now();
        LocalDate sessionEnd = LocalDate.of(2024, 4, 29);

        UserLogs userLogs = new UserLogs(id, user, sessionStart,sessionTimeStart, sessionEnd, sessionEndTime);

        assertEquals(id, userLogs.getId());
        assertEquals(user, userLogs.getUser());
        assertEquals(sessionStart, userLogs.getSessionStartDate());
        assertEquals(sessionEnd, userLogs.getSessionEndDate());
    }

    @Test
    void testSetterMethods() {
        UserLogs userLogs = new UserLogs();
        User user = new User();
        LocalDate sessionStart = LocalDate.of(2024, 4, 28);
        LocalDate sessionEnd = LocalDate.of(2024, 4, 29);

        Long id = 2L;
        userLogs.setId(id);
        userLogs.setUser(user);
        userLogs.setSessionStartDate(sessionStart);
        userLogs.setSessionEndDate(sessionEnd);

        assertEquals(id, userLogs.getId());
        assertEquals(user, userLogs.getUser());
        assertEquals(sessionStart, userLogs.getSessionStartDate());
        assertEquals(sessionEnd, userLogs.getSessionEndDate());
    }

}