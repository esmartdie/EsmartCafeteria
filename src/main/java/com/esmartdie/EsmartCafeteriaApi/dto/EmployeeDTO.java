package com.esmartdie.EsmartCafeteriaApi.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters long")
    private String password;

    @AssertTrue
    private boolean active;

    @NotNull(message = "Employee ID is required")
    private Long employee_id;

    public EmployeeDTO(){};

    public EmployeeDTO(String name, String lastName, String email, String password, boolean active, Long employee_id) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.active = active;
        this.employee_id = employee_id;
    }

    public EmployeeDTO(String name, String lastName, String email, Boolean active, Long employeeId) {
        setName(name);
        setLastName(lastName);
        setEmail(email);
        setActive(active);
        setEmployee_id(employeeId);
    }
}
