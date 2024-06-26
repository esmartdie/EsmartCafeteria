package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeDTO;
import com.esmartdie.EsmartCafeteriaApi.dto.EmployeeResponseDTO;
import com.esmartdie.EsmartCafeteriaApi.service.user.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employee")
public class EmployeeController implements IEmployeeController{

    @Autowired
    private IEmployeeService employeeService;
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployeeActive() {
        List<EmployeeResponseDTO> activeEmployees = employeeService.getActiveEmployees();
        return ResponseEntity.ok(activeEmployees);
    }
    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployeeInactive() {
        List<EmployeeResponseDTO> inactiveEmployees = employeeService.getInactiveEmployees();
        return ResponseEntity.ok(inactiveEmployees);
    }
    @PatchMapping("/{employeeId}/updateStatus")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void updateEmployeeStatus(@PathVariable Long employeeId, @RequestParam boolean isActive) {

        employeeService.updateEmployeeStatus(employeeId, isActive);
    }

}
