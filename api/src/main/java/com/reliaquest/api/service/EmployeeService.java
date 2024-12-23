package com.reliaquest.api.service;

import static com.reliaquest.api.common.Constants.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.client.EmployeeServiceClient;
import com.reliaquest.api.common.EmployeeDeleteRequest;
import com.reliaquest.api.common.EmployeeRequest;
import com.reliaquest.api.model.Employee;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeService {

    private static final String CACHE_KEY = "allEmployees";
    private final EmployeeServiceClient employeeServiceClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, com.reliaquest.api.common.Response<List<Employee>>> cache =
            new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public EmployeeService(@NonNull final EmployeeServiceClient employeeServiceClient) {
        Objects.requireNonNull(employeeServiceClient, "EmployeeServiceClient must not be null");
        this.employeeServiceClient = employeeServiceClient;
    }

    /**
     * Get all employees from service.
     *
     * <p>This method is cached for 10 minutes, so subsequent calls will return the cached result.
     * </p>
     *
     * @return a {@link com.reliaquest.api.common.Response} containing a list of all employees
     */
    public com.reliaquest.api.common.Response<List<Employee>> getAllEmployees() {
        com.reliaquest.api.common.Response<List<Employee>> cachedResult = cache.get(CACHE_KEY);
        if (cachedResult != null) {
            return cachedResult;
        }

        com.reliaquest.api.common.Response<List<Employee>> result = fetchAllEmployees();
        cache.put(CACHE_KEY, result);
        scheduler.schedule(() -> cache.remove(CACHE_KEY), 10, TimeUnit.MINUTES);
        return result;
    }

    /**
     * Search for employees by name.
     *
     * <p>This method will return a list of all employees whose name matches the given search string.
     * </p>
     *
     * @param searchString the name to search for
     * @return a {@link com.reliaquest.api.common.Response} containing a list of all matching employees
     */
    public com.reliaquest.api.common.Response<List<Employee>> searchByEmployeeName(@NonNull final String searchString) {
        List<Employee> data = getAllEmployees().data();
        if (data == null) {
            return com.reliaquest.api.common.Response.error("No employees found");
        }
        List<Employee> list = Optional.of(getAllEmployees().data().stream()
                .filter(Objects::nonNull)
                .filter(employee -> employee.getName().contains(searchString))
                .toList()).orElse(Collections.emptyList());
        if (list.isEmpty()) {
            return com.reliaquest.api.common.Response.error("No employees found");
        } else {
            return com.reliaquest.api.common.Response.handledWith(list);
        }
    }

    /**
     * Retrieves an employee by their ID.
     *
     * <p>This method sends a GET request to the employee service to fetch
     * the employee with the specified ID. If the employee is found, it returns
     * a {@link com.reliaquest.api.common.Response} containing the employee data.
     * If there is an error during the request, it logs the error and returns
     * an error response.</p>
     *
     * @param id the ID of the employee to retrieve
     * @return a {@link com.reliaquest.api.common.Response} containing the employee data,
     *         or an error response if the employee is not found or an error occurs
     */
    public com.reliaquest.api.common.Response<Employee> getEmployeeById(@NonNull final String id) {
        String format = String.format(PATH_ID).replace("{id}", id);
        try (Response response = employeeServiceClient.get(PATH_EMPLOYEE + format)) {
            return handleResponse(response);
        } catch (IOException e) {
            log.error(e.getMessage());
            return handleException(e);
        }
    }

    /**
     * Retrieves the highest salary of all employees.
     *
     * <p>This method first fetches all employees, then streams them to find the
     * highest salary. If there are no employees, it returns 0.</p>
     *
     * @return the highest salary of all employees
     */
    public Integer getHighestSalaryOfEmployees() {
        return getAllEmployees().data().stream()
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
    }

    /**
     * Retrieves the names of the top ten highest earning employees.
     *
     * <p>This method fetches all employees and sorts them in descending order
     * based on their salaries. It then limits the result to the top ten employees
     * and maps them to their names.</p>
     *
     * @return a list of names of the top ten highest earning employees
     */
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().data().stream()
                .sorted((e1, e2) -> e2.getSalary() - e1.getSalary())
                .limit(10)
                .map(Employee::getName)
                .toList();
    }

    /**
     * Creates a new employee in the service.
     *
     * <p>This method will send a POST request to the employee service with the given employee input.
     * If the request is successful, it will return a {@link com.reliaquest.api.common.Response} containing
     * the newly created employee. If there is an error during the request, it will return an error response.
     * </p>
     *
     * @param employeeInput the employee data to create
     * @return a {@link com.reliaquest.api.common.Response} containing the newly created employee or an error response
     */
    public com.reliaquest.api.common.Response<Employee> createEmployee(Object employeeInput) {
        EmployeeRequest employeeRequest = objectMapper.convertValue(employeeInput, EmployeeRequest.class);

        try (Response response =
                employeeServiceClient.post(PATH_EMPLOYEE, objectMapper.writeValueAsString(employeeRequest))) {
            if (response.isSuccessful()) {
                return handleResponse(response);
            } else {
                return buildError(response);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return handleException(e);
        }
    }

    /**
     * Deletes an employee by their ID.
     *
     * <p>This method will find the employee with the given ID and then send a DELETE request to the
     * employee service to delete the employee. If the request is successful, it will return a
     * {@link com.reliaquest.api.common.Response} containing the deleted employee. If there is an error
     * during the request, it will return an error response.</p>
     *
     * @param employeeId the ID of the employee to delete
     * @return a {@link com.reliaquest.api.common.Response} containing the deleted employee or an error response
     */
    public com.reliaquest.api.common.Response<Employee> deleteEmployeeById(@NonNull final String employeeId) {
        Employee employee = getAllEmployees().data().stream()
                .filter(e -> e.getId().equals(employeeId))
                .findFirst()
                .orElse(null);

        if (employee == null) {
            return com.reliaquest.api.common.Response.error("Employee not found");
        }
        String employeeName = employee.getName();
        try (Response response = employeeServiceClient.delete(
                PATH_EMPLOYEE, objectMapper.writeValueAsString(new EmployeeDeleteRequest(employeeName)))) {
            if (response.isSuccessful()) {
                return com.reliaquest.api.common.Response.handledWith(employee);
            } else {
                return buildError(response);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return handleException(e);
        }
    }

    private com.reliaquest.api.common.Response<List<Employee>> fetchAllEmployees() {
        try (Response response = employeeServiceClient.get(PATH_EMPLOYEE)) {
            return handleResponseArray(response);
        } catch (IOException e) {
            log.error(e.getMessage());
            return handleExceptionArray(e);
        }
    }

    private com.reliaquest.api.common.Response<Employee> handleResponse(Response response) {
        ResponseBody body = response.body();
        if (response.isSuccessful() && body != null) {
            try {
                JsonNode jsonNode = objectMapper.readTree(body.string());
                JsonNode data = jsonNode.get("data");
                if (!data.isArray()) {
                    Employee employee = objectMapper.readValue(data.toString(), Employee.class);
                    return com.reliaquest.api.common.Response.handledWith(employee);
                }
                log.error("incompatible data: " + data);
                return com.reliaquest.api.common.Response.error("incompatible data");
            } catch (IOException e) {
                log.error(e.getMessage());
                return handleException(e);
            }
        } else {
            return buildError(response);
        }
    }

    private com.reliaquest.api.common.Response<List<Employee>> handleResponseArray(Response response) {
        ResponseBody body = response.body();
        if (response.isSuccessful() && body != null) {
            try {
                com.reliaquest.api.common.Response<List<Employee>> result =
                        objectMapper.readValue(body.string(), new TypeReference<>() {});
                if (result.data() != null) {
                    return com.reliaquest.api.common.Response.handledWith(result.data());
                }
                return com.reliaquest.api.common.Response.handledWith(Collections.emptyList());
            } catch (IOException e) {
                log.error(e.getMessage());
                return handleExceptionArray(e);
            }
        } else {
            return buildErrorArray(response);
        }
    }

    private com.reliaquest.api.common.Response<List<Employee>> buildErrorArray(Response response) {
        return com.reliaquest.api.common.Response.error(response.code() + " " + response.message());
    }

    private com.reliaquest.api.common.Response<Employee> buildError(Response response) {
        return com.reliaquest.api.common.Response.error(response.code() + " " + response.message());
    }

    private com.reliaquest.api.common.Response<List<Employee>> handleExceptionArray(IOException e) {
        return com.reliaquest.api.common.Response.error(e.getMessage());
    }

    private com.reliaquest.api.common.Response<Employee> handleException(IOException e) {
        return com.reliaquest.api.common.Response.error(e.getMessage());
    }
}
