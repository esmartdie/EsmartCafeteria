package com.esmartdie.EsmartCafeteriaApi.controller.user;

import com.esmartdie.EsmartCafeteriaApi.model.user.Employee;
import com.esmartdie.EsmartCafeteriaApi.service.user.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController implements IEmployeeController{

    @Autowired
    private IEmployeeService employeeService;

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */
    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<Employee> getAllActive() {
        return employeeService.getActiveEmployees().get();
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */
    @GetMapping("/inactive")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<Employee> getAllInactive() {
        return employeeService.getInactiveEmployees().get();
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/{employeeId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void activateClient(@PathVariable Long employeeId) {
        employeeService.activateEmployee(employeeId);
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @PatchMapping("/{employeeId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void deactivateClient(@PathVariable Long employeeId) {
        employeeService.activateEmployee(employeeId);
    }

    /**
     * TODO refactor and postman test
     * @param id
     * @return
     */

    @DeleteMapping("/{employeeId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void updateClientRating(@RequestBody Employee employee) {
        employeeService.deleteEmployee(employee.getId());
    }

}
