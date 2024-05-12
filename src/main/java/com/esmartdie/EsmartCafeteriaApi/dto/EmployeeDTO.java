package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean active;
    private Long employee_id;

    public EmployeeDTO(){};

    public EmployeeDTO(String name, String lastName, String email, Boolean active, Long employeeId) {
        this.name=name;
        this.lastName=lastName;
        this.email=email;
        this.active=active;
        this.employee_id=employeeId;
    }

    public EmployeeDTO(Long id, String name, String lastName, String email, Boolean active, Long employeeId) {
        this.id = id;
        this.name=name;
        this.lastName=lastName;
        this.email=email;
        this.active=active;
        this.employee_id=employeeId;
    }

}
