package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

@Data
public class EmployeeResponseDTO {

    private Long id;

    private String name;

    private String lastName;

    private String email;

    private boolean active;

    private Long employee_id;
    public EmployeeResponseDTO(){};

    public EmployeeResponseDTO(Long id, String name, String lastName, String email, boolean active, Long employee_id) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.employee_id = employee_id;
    }
}
