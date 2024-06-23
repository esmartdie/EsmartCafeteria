package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.SessionActiveNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserLogsRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLogsServiceTest {

    @Mock
    private IUserLogsRepository userLogsRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UserLogsService userLogsService;

    @Test
    void createUserLoginLog_UserExists() {
        User user = new User();
        user.setEmail("john.doe@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userLogsRepository.save(any(UserLogs.class))).thenAnswer(i -> i.getArgument(0));

        UserLogs userLogs = userLogsService.createUserLoginLog("john.doe@example.com");

        assertEquals(user, userLogs.getUser());
        assertEquals(LocalDate.now(), userLogs.getSessionStartDate());
        assertEquals(LocalTime.now().getHour(), userLogs.getSessionStartTime().getHour());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userLogsRepository, times(1)).save(any(UserLogs.class));
    }

    @Test
    void createUserLoginLog_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userLogsService.createUserLoginLog("john.doe@example.com"));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userLogsRepository, times(0)).save(any(UserLogs.class));
    }

    @Test
    void createUserLogoutLog_SessionExists() {
        User user = new User();
        UserLogs userLog = new UserLogs();
        userLog.setUser(user);
        userLog.setSessionStartDate(LocalDate.now());
        userLog.setSessionStartTime(LocalTime.now());

        when(userLogsRepository.findLastUserSession(any(User.class))).thenReturn(Collections.singletonList(userLog));

        userLogsService.createUserLogoutLog(user);

        verify(userLogsRepository, times(1)).findLastUserSession(any(User.class));
        verify(userLogsRepository, times(1)).save(userLog);
    }


    @Test
    void createUserLogoutLog_SessionDoesNotExist() {
        User user = new User();
        UserLogs userLog = new UserLogs();
        userLog.setUser(user);
        userLog.setSessionStartDate(LocalDate.now());
        userLog.setSessionStartTime(LocalTime.now());
        userLog.setSessionEndDate(LocalDate.now());
        userLog.setSessionEndTime(LocalTime.now());

        when(userLogsRepository.findLastUserSession(any(User.class))).thenReturn(Collections.singletonList(userLog));

        assertThrows(SessionActiveNotFoundException.class, () -> userLogsService.createUserLogoutLog(user));

        verify(userLogsRepository, times(1)).findLastUserSession(any(User.class));
        verify(userLogsRepository, times(0)).save(any(UserLogs.class));
    }
}