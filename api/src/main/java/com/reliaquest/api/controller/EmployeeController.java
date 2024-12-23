package com.reliaquest.api.controller;

import static com.reliaquest.api.common.Constants.*;

import com.reliaquest.api.common.Response;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = PATH_EMPLOYEE, produces = APPLICATION_JSON)
public class EmployeeController implements IEmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(@NonNull final EmployeeService employeeService) {
        Objects.requireNonNull(employeeService, "EmployeeService must not be null");
        this.employeeService = employeeService;
    }

    /**
     * Retrieves a list of all employees.
     *
     * @return ResponseEntity containing a list of all employees.
     *         If the list is empty, returns a 204 No Content response.
     *         If there is an error in retrieving the list, returns a 400 Bad Request response.
     */
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        Response<List<Employee>> allEmployees = employeeService.getAllEmployees();
        return getListResponseEntity(allEmployees);
    }

    /**
     * Searches for employees by name.
     *
     * @param searchString the search string to look for in employee names.
     * @return ResponseEntity containing a list of employees whose name matches the given search string.
     *         If the list is empty, returns a 204 No Content response.
     *         If there is an error in retrieving the list, returns a 400 Bad Request response.
     */
    @GetMapping(value = PATH_SEARCH)
    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@NonNull final String searchString) {
        Response<List<Employee>> listResponse = employeeService.searchByEmployeeName(searchString);
        return getListResponseEntity(listResponse);
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id the ID of the employee to retrieve.
     * @return ResponseEntity containing the employee with the given ID.
     *         If there is an error in retrieving the employee, returns a 400 Bad Request response.
     *         If the employee is not found, returns a 404 Not Found response.
     */
    @GetMapping(value = PATH_ID)
    @Override
    public ResponseEntity<Employee> getEmployeeById(@NonNull final String id) {
        Response<Employee> employeeById = employeeService.getEmployeeById(id);
        return getEmployeeResponse(employeeById);
    }

    /**
     * Retrieves the highest salary among all employees.
     *
     * @return ResponseEntity containing the highest salary as an Integer.
     *         If no employees are found, returns a 204 No Content response.
     */
    @GetMapping(value = PATH_HIGHEST_SALARY)
    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalaryOfEmployees = employeeService.getHighestSalaryOfEmployees();
        if (highestSalaryOfEmployees == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(highestSalaryOfEmployees);
    }

    /**
     * Retrieves the names of the top ten highest earning employees.
     *
     * @return ResponseEntity containing a list of the names of the top ten highest earning employees.
     *         If the list is empty, returns a 204 No Content response.
     */
    @GetMapping(value = PATH_TOP_TEN_HIGHEST_EARNING_EMPLOYEE_NAMES)
    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topTenHighestEarningEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();
        if (topTenHighestEarningEmployeeNames != null) {
            return ResponseEntity.ok(topTenHighestEarningEmployeeNames);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Creates a new employee with the given request body.
     *
     * @param employeeInput the request body containing the new employee's information.
     * @return ResponseEntity containing the newly created employee.
     *         If the input is invalid, returns a 400 Bad Request response.
     *         If there is an error in creating the employee, returns a 500 Internal Server Error response.
     */
    @PostMapping
    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody @NonNull final Object employeeInput) {
        Response<Employee> employee = employeeService.createEmployee(employeeInput);
        return getEmployeeResponse(employee);
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id the ID of the employee to delete.
     * @return ResponseEntity containing the name of the deleted employee.
     *         If the employee is not found, returns a 404 Not Found response.
     *         If there is an error in deleting the employee, returns a 500 Internal Server Error response.
     */
    @DeleteMapping(value = PATH_ID)
    @Override
    public ResponseEntity<String> deleteEmployeeById(@NonNull final String id) {
        Response<Employee> employeeResponse = employeeService.deleteEmployeeById(id);
        String employeeName = employeeResponse.data().getName();
        if (employeeName == null) {
            return ResponseEntity.notFound().build(); // 204 or 404? adjust to consuming application needs
        } else {
            return ResponseEntity.ok(employeeName);
        }
    }

    @NotNull private static ResponseEntity<List<Employee>> getListResponseEntity(
            @Nullable Response<List<Employee>> allEmployees) {
        if (allEmployees != null) {
            if (null == allEmployees.data() || allEmployees.data().isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(allEmployees.data());
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @NotNull private static ResponseEntity<Employee> getEmployeeResponse(@Nullable Response<Employee> employeeResponse) {
        if (employeeResponse != null) {
            if (null == employeeResponse.data()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(employeeResponse.data());
            }
        } else {
            return ResponseEntity.notFound().build(); // 204 or 404? adjust to consuming application needs
        }
    }
}
