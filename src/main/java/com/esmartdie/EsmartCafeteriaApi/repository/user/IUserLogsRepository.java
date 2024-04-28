package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.UserLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserLogsRepository extends JpaRepository<UserLogs, Long> {
}
