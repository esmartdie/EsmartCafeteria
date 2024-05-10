package com.esmartdie.EsmartCafeteriaApi.dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private boolean active;
    private Long employee_id;
}
