package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IEmployeeController {

    ResponseEntity<List<EmployeeDTO>> getAllEmployeeActive();

    ResponseEntity<List<EmployeeDTO>> getAllEmployeeInactive();

    void updateEmployeeStatus(@PathVariable Long employeeId, @RequestParam boolean isActive);
}
