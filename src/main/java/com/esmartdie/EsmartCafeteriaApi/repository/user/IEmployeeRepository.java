package com.esmartdie.EsmartCafeteriaApi.repository.user;


import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IEmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAll();

    @Query("SELECT c FROM Employee c WHERE c.active = true")
    List<Employee> findAllActive();

    @Query("SELECT c FROM Employee c WHERE c.active = false")
    List<Employee> findAllInactive();
}
