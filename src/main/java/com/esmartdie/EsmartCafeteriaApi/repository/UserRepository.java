package com.esmartdie.EsmartCafeteriaApi.repository;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional <User> findById(Long id);
}
