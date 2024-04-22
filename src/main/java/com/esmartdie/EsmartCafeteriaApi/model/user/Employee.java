package com.esmartdie.EsmartCafeteriaApi.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Employee extends User{

    private Long employee_id;

    public Employee(Long id, String name, String lastName, String email,
                    String password, Boolean active, Role role, Long employee_id) {
        super(id, name, lastName, email, password, active, role);
        this.employee_id=employee_id;
        setRole(new Role(null,"ROLE_MODERATOR"));
    }
}
