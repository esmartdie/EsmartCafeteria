package com.esmartdie.EsmartCafeteriaApi.service.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;

public interface IUserLogsService {

    UserLogs createUserLoginLog(User user);

    void createUserLogoutLog(User user);
}
