package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUserLogsRepository extends JpaRepository<UserLogs, Long> {

    @Query("SELECT ul FROM UserLogs ul WHERE ul.user = :user and sessionEnd IS NULL ")
    UserLogs findLastUserSession(@Param("user") User user);
}
