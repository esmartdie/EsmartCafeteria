package com.esmartdie.EsmartCafeteriaApi.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("Employee")
public class Employee extends User{

    private Long employee_id;

    public Employee(Long id, String name, String lastName, String email,
                    String password, Boolean active, Role role, Long employee_id) {
        super(id, name, lastName, email, password, active, role);
        this.employee_id=employee_id;
    }
}
