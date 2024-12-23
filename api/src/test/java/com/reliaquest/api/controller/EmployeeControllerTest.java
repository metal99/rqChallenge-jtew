package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.reliaquest.api.common.Response;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

class EmployeeControllerTest {

    private static final String SEARCH_STRING = "Lowell";
    private static final String SEARCH_ID = "d3e4d6e8-fb10-4350-a340-7f6f84574d50";

    @Mock
    private EmployeeService employeeServiceMock;

    private EmployeeController employeeController;
    private Response<List<Employee>> employeeResponse;
    private Employee employee, employee2;
    private String json;

    @BeforeEach
    void setUp() {
        employeeServiceMock = mock(EmployeeService.class);
        employeeController = new EmployeeController(employeeServiceMock);

        buildAllEmployees();
    }

    private void buildAllEmployees() {
        employee = Employee.builder()
                .id("d3e4d6e8-fb10-4350-a340-7f6f84574d50")
                .name("Lowell Willms II")
                .salary(58633)
                .age(68)
                .title("Community-Services Manager")
                .email("zaam-dox@company.com")
                .build();
        employee2 = Employee.builder()
                .id("f97290eb-82b6-4966-9ad5-1d475c63e858")
                .name("Terence Considine")
                .salary(346280)
                .age(62)
                .title("IT Liaison")
                .email("northernlightz@company.com")
                .build();

        employeeResponse = new Response<>(List.of(employee, employee2), Response.Status.HANDLED, null);

        json = "{\n" + "    \"data\": [\n"
                + "        {\n"
                + "            \"id\": \"213c4e6d-80d3-4700-803b-7e3ff1c7401c\",\n"
                + "            \"employee_name\": \"Ricky Keeling\",\n"
                + "            \"employee_salary\": 398286,\n"
                + "            \"employee_age\": 69,\n"
                + "            \"employee_title\": \"Regional Education Consultant\",\n"
                + "            \"employee_email\": \"lotlux@company.com\"\n"
                + "        },\n"
                + "        {\n"
                + "            \"id\": \"87608815-a852-4905-8f1c-ee5b1460a48a\",\n"
                + "            \"employee_name\": \"Val Rau\",\n"
                + "            \"employee_salary\": 265917,\n"
                + "            \"employee_age\": 41,\n"
                + "            \"employee_title\": \"Advertising Planner\",\n"
                + "            \"employee_email\": \"mcshayne@company.com\"\n"
                + "        },\n"
                + "    ],\n"
                + "    \"status\": \"Successfully processed request.\"\n"
                + "}";
    }

    @DisplayName("Get all employees, 2 results")
    @Test
    void getAllEmployees() {
        when(employeeServiceMock.getAllEmployees()).thenReturn(employeeResponse);

        ResponseEntity<List<Employee>> actual = employeeController.getAllEmployees();

        assertNotNull(actual);
        assertEquals(2, actual.getBody().size());
        verify(employeeServiceMock, times(1)).getAllEmployees();
    }

    @DisplayName("Get all employees, error")
    @Test
    void getAllEmployeesError() {
        when(employeeServiceMock.getAllEmployees()).thenReturn(null);

        ResponseEntity<List<Employee>> actual = employeeController.getAllEmployees();

        assertNotNull(actual);
        assertEquals(400, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getAllEmployees();
    }

    @DisplayName("Get all employees, no content")
    @Test
    void getAllEmployeesNoContent() {
        Response<List<Employee>> response = new Response<>(Collections.emptyList(), Response.Status.HANDLED, null);
        when(employeeServiceMock.getAllEmployees()).thenReturn(response);

        ResponseEntity<List<Employee>> actual = employeeController.getAllEmployees();

        assertNotNull(actual);
        assertEquals(204, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getAllEmployees();
    }

    @DisplayName("Search by employee name, 1 results")
    @Test
    void getEmployeesByNameSearch() {
        Response<List<Employee>> employeeResponse = new Response<>(List.of(employee), Response.Status.HANDLED, null);
        when(employeeServiceMock.searchByEmployeeName(anyString())).thenReturn(employeeResponse);

        ResponseEntity<List<Employee>> actual = employeeController.getEmployeesByNameSearch(SEARCH_STRING);

        assertNotNull(actual);
        assertEquals(1, actual.getBody().size());
        verify(employeeServiceMock, times(1)).searchByEmployeeName(anyString());
    }

    @DisplayName("Search by employee name, no results")
    @Test
    void getEmployeesByNameSearchNoResults() {
        Response<List<Employee>> employeeResponse =
                new Response<>(Collections.emptyList(), Response.Status.HANDLED, null);
        when(employeeServiceMock.searchByEmployeeName(anyString())).thenReturn(employeeResponse);

        ResponseEntity<List<Employee>> actual = employeeController.getEmployeesByNameSearch(SEARCH_STRING);

        assertNotNull(actual);
        assertEquals(204, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).searchByEmployeeName(anyString());
    }

    @DisplayName("Search by employee Id, no results")
    @Test
    void getEmployeeById() {
        when(employeeServiceMock.getEmployeeById(anyString())).thenReturn(null);

        ResponseEntity<Employee> actual = employeeController.getEmployeeById(SEARCH_ID);

        assertNotNull(actual);
        assertEquals(404, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getEmployeeById(anyString());
    }

    @DisplayName("Search by employee Id, 1 results")
    @Test
    void getEmployeeByIdSuccess() {
        Response<Employee> employeeResponse = new Response<>(employee, Response.Status.HANDLED, null);
        when(employeeServiceMock.getEmployeeById(anyString())).thenReturn(employeeResponse);

        ResponseEntity<Employee> actual = employeeController.getEmployeeById(SEARCH_ID);

        assertNotNull(actual);
        assertEquals(200, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getEmployeeById(anyString());
    }

    @Test
    void getHighestSalaryOfEmployees() {
        when(employeeServiceMock.getHighestSalaryOfEmployees()).thenReturn(50000);
        ResponseEntity<Integer> actual = employeeController.getHighestSalaryOfEmployees();

        assertNotNull(actual);
        assertEquals(50000, actual.getBody());
        assertEquals(200, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    void getHighestSalaryOfEmployeesNoContent() {
        when(employeeServiceMock.getHighestSalaryOfEmployees()).thenReturn(null);
        ResponseEntity<Integer> actual = employeeController.getHighestSalaryOfEmployees();

        assertNotNull(actual);
        assertEquals(204, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames() {
        when(employeeServiceMock.getTopTenHighestEarningEmployeeNames()).thenReturn(List.of("a,b,c,d"));
        ResponseEntity<List<String>> actual = employeeController.getTopTenHighestEarningEmployeeNames();

        assertNotNull(actual);
        assertEquals(List.of("a,b,c,d"), actual.getBody());
        assertEquals(200, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void getTopTenHighestEarningEmployeeNamesNoContent() {
        when(employeeServiceMock.getTopTenHighestEarningEmployeeNames()).thenReturn(null);
        ResponseEntity<List<String>> actual = employeeController.getTopTenHighestEarningEmployeeNames();

        assertNotNull(actual);
        assertEquals(204, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    void createEmployee() {
        when(employeeServiceMock.createEmployee(any())).thenReturn(Response.handledWith(employee));
        ResponseEntity<Employee> actual = employeeController.createEmployee(employee);

        assertNotNull(actual);
        assertEquals(200, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).createEmployee(any());
    }

    @Test
    void createEmployeeBadRequest() {
        when(employeeServiceMock.createEmployee(any())).thenReturn(null);
        ResponseEntity<Employee> actual = employeeController.createEmployee(employee);

        assertNotNull(actual);
        assertEquals(404, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).createEmployee(any());
    }

    @Test
    void createEmployeeBadRequestNoContent() {
        when(employeeServiceMock.createEmployee(any())).thenReturn(Response.handledWith(null));
        ResponseEntity<Employee> actual = employeeController.createEmployee(employee);

        assertNotNull(actual);
        assertEquals(204, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).createEmployee(any());
    }

    @Test
    void deleteEmployeeById() {
        when(employeeServiceMock.deleteEmployeeById(any())).thenReturn(Response.handledWith(employee));
        ResponseEntity<String> actual = employeeController.deleteEmployeeById(SEARCH_ID);

        assertNotNull(actual);
        assertEquals(200, actual.getStatusCode().value());
        assertEquals(employee.getName(), actual.getBody());
        verify(employeeServiceMock, times(1)).deleteEmployeeById(any());
    }

    @Test
    void deleteEmployeeByIdNotFound() {
        employee.setName(null);
        when(employeeServiceMock.deleteEmployeeById(any())).thenReturn(Response.handledWith(employee));
        ResponseEntity<String> actual = employeeController.deleteEmployeeById(SEARCH_ID);

        assertNotNull(actual);
        assertEquals(404, actual.getStatusCode().value());
        verify(employeeServiceMock, times(1)).deleteEmployeeById(any());
    }
}
