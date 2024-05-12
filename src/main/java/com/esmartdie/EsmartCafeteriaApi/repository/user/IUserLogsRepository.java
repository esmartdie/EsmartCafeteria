package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUserLogsRepository extends JpaRepository<UserLogs, Long> {

    @Query("SELECT ul FROM UserLogs ul WHERE ul.user = :user  ORDER BY ul.id DESC")
    List<UserLogs> findLastUserSession(@Param("user") User user);
}
