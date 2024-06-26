package com.esmartdie.EsmartCafeteriaApi.model.user;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("Employee")
public class Employee extends User{

    @Positive
    @NotNull
    private Long employee_id;

    public Employee(Long id, String name, String lastName, String email,
                    String password, Boolean active, Role role, Long employee_id) {
        super(id, name, lastName, email, password, active, role);
        this.employee_id=employee_id;
    }
}
