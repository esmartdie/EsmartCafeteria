package com.esmartdie.EsmartCafeteriaApi.repository.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);
}
