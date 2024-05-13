package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.exception.ResourceNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.exception.SessionActiveNotFoundException;
import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserLogsRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogsService implements IUserLogsService{

    @Autowired
    private IUserLogsRepository userLogsRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public UserLogs createUserLoginLog(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found with email: " + email));
        UserLogs userLog = new UserLogs();
        userLog.setUser(user);
        userLog.setSessionStartDate(LocalDate.now());
        userLog.setSessionStartTime(LocalTime.now());
        log.info("Saving new user {} login on the database", userLog.getUser());
        return userLogsRepository.save(userLog);
    }

    @Override
    public void createUserLogoutLog(User user) {
        UserLogs userLog = userLogsRepository.findLastUserSession(user).stream().findFirst().get();
        if(userLog.getSessionEndDate()==null){
            userLog.setSessionEndDate(LocalDate.now());
            userLog.setSessionEndTime(LocalTime.now());
        }else{
            throw new SessionActiveNotFoundException("No active session to logout");
        }
        log.info("Saving  user{} logout to the database", userLog.getUser());
        userLogsRepository.save(userLog);
    }

}
