package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IEmployeeController {

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<EmployeeDTO>> getAllActive();

    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<EmployeeDTO>>  getAllInactive();

    @PatchMapping("/{employeeId}/updateStatus")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateEmployeeStatus(@PathVariable Long employeeId, @RequestParam boolean isActive);
}
