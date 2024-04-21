package com.esmartdie.EsmartCafeteriaApi.repository;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
