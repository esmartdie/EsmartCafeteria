package com.esmartdie.EsmartCafeteriaApi.repository;

import com.esmartdie.EsmartCafeteriaApi.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

}
