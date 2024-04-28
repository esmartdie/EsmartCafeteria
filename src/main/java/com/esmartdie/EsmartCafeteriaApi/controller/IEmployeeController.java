package com.esmartdie.EsmartCafeteriaApi.controller;

import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IEmployeeController {
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    List<Employee> getAllActive();

    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    List<Employee> getAllInactive();

    @PatchMapping("/{employeeId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void activateClient(@PathVariable Long employeeId);

    @PatchMapping("/{employeeId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateClient(@PathVariable Long employeeId);

    @DeleteMapping("/{employeeId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateClientRating(@RequestBody Employee employee);
}
