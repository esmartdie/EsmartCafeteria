package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserLogsService implements IUserLogsService{

    @Autowired
    private IUserLogsRepository userLogsRepository;

    @Override
    public UserLogs createUserLog(UserLogs uLog){
        log.info("Saving new user{} log to the database", uLog.getUser());
        return userLogsRepository.save(uLog);
    }

}
