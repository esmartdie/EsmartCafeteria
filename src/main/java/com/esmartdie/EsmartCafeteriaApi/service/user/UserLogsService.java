package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogsService implements IUserLogsService{

    @Autowired
    private IUserLogsRepository userLogsRepository;

    @Override
    public UserLogs createUserLoginLog(User user){
        UserLogs userLog = new UserLogs();
        userLog.setUser(user);
        userLog.setSessionStart(LocalDate.now());
        log.info("Saving new user{} login to the database", userLog.getUser());
        return userLogsRepository.save(userLog);
    }

    @Override
    public void createUserLogoutLog(User user) {
        UserLogs userLog = userLogsRepository.findLastUserSession(user);
        userLog.setUser(user);
        userLog.setSessionStart(userLog.getSessionStart());
        userLog.setSessionEnd(LocalDate.now());
        log.info("Saving  user{} logout to the database", userLog.getUser());
        userLogsRepository.save(userLog);
    }

}
