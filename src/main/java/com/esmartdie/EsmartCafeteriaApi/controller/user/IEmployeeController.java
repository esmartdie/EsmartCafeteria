package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IEmployeeController {

    ResponseEntity<List<EmployeeDTO>> getAllActive();

    ResponseEntity<List<EmployeeDTO>>  getAllInactive();

    void updateEmployeeStatus(@PathVariable Long employeeId, @RequestParam boolean isActive);
}
