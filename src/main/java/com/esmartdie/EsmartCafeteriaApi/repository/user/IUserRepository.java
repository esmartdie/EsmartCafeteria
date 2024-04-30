package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {

    User findByName(String username);
    Optional<User> findByEmail(String email);
    Optional <User> findById(Long id);
}
