package com.reliaquest.api.service;

import static com.reliaquest.api.common.Constants.PATH_EMPLOYEE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.client.EmployeeServiceClient;
import com.reliaquest.api.common.EmployeeRequest;
import com.reliaquest.api.model.Employee;
import java.io.IOException;
import java.util.*;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class EmployeeServiceTest {

    private static final String EXCEPTION_MESSAGE = "exception message";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployeeServiceClient employeeServiceClientMock;

    @Mock
    private Response responseMock;

    @Mock
    private ResponseBody responseBodyMock;

    @Mock
    private IOException exceptionMock;

    private EmployeeService employeeService;
    private Employee employee, employee2;

    @BeforeEach
    void setUp() throws IOException {
        responseMock = mock(Response.class);
        responseBodyMock = mock(ResponseBody.class);
        exceptionMock = mock(IOException.class);
        when(responseMock.body()).thenReturn(responseBodyMock);
        when(responseMock.isSuccessful()).thenReturn(true);
        when(exceptionMock.getMessage()).thenReturn(EXCEPTION_MESSAGE);

        employeeServiceClientMock = mock(EmployeeServiceClient.class);
        when(employeeServiceClientMock.get(any())).thenReturn(responseMock);
        when(employeeServiceClientMock.post(any(), any())).thenReturn(responseMock);

        employeeService = new EmployeeService(employeeServiceClientMock);

        buildEmployees();
    }

    private void buildEmployees() throws IOException {
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

        com.reliaquest.api.common.Response<List<Employee>> cachedResponse =
                com.reliaquest.api.common.Response.handledWith(List.of(employee, employee2));
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(cachedResponse));
        when(responseMock.body()).thenReturn(responseBodyMock);
        when(employeeServiceClientMock.get(PATH_EMPLOYEE)).thenReturn(responseMock);

    }

    @DisplayName("Get all employees, no results")
    @Test
    void getAllEmployees() throws IOException {
        when(responseMock.body()).thenReturn(null);

        com.reliaquest.api.common.Response<List<Employee>> expected =
                com.reliaquest.api.common.Response.error("0 null"); // undefined exception result listed
        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.getAllEmployees();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("Get all employees, body null but success")
    @Test
    void getAllEmployeesBodyNull() throws IOException {
        when(responseMock.body()).thenReturn(null);
        when(responseMock.isSuccessful()).thenReturn(false);

        com.reliaquest.api.common.Response<List<Employee>> expected =
                com.reliaquest.api.common.Response.error("0 null"); // undefined exception result listed

        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.getAllEmployees();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
        verify(responseBodyMock, times(0)).string();
    }

    @DisplayName("Get all employees, 2 result")
    @Test
    void getAllEmployeesTwoResult() throws IOException {
        com.reliaquest.api.common.Response<List<Employee>> expected =
                com.reliaquest.api.common.Response.handledWith(List.of(employee, employee2));
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(expected));

        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.getAllEmployees();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("Get all employees, error")
    @Test
    void getAllEmployeesError() throws IOException {
        when(employeeServiceClientMock.get(any())).thenThrow(exceptionMock);

        com.reliaquest.api.common.Response<List<Employee>> expected =
                com.reliaquest.api.common.Response.error(EXCEPTION_MESSAGE);

        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.getAllEmployees();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
    }

    @DisplayName("Search by employee name, no results")
    @Test
    void searchByEmployeeName() throws IOException {
         com.reliaquest.api.common.Response<List<Employee>> expected =
                com.reliaquest.api.common.Response.error("No employees found");

        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.searchByEmployeeName("wrong name");

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("Search by employee name, 1 result")
    @Test
    void searchByEmployeeNameOneResult() throws IOException {
        com.reliaquest.api.common.Response<List<Employee>> expected =
                com.reliaquest.api.common.Response.handledWith(List.of(employee));
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(expected));

        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.searchByEmployeeName("Lowell");

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("Search by employee name, error")
    @Test
    void searchByEmployeeNameError() throws IOException {
        when(employeeServiceClientMock.get(any())).thenThrow(exceptionMock);

        com.reliaquest.api.common.Response<Object> expected =
                com.reliaquest.api.common.Response.error("No employees found");

        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.searchByEmployeeName("Lowell");

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
    }

    @DisplayName("find employee by id")
    @Test
    void findEmployeeById() throws IOException {
        com.reliaquest.api.common.Response<Employee> expected =
                com.reliaquest.api.common.Response.handledWith(employee);
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(expected));
        String searchIdValue = employee.getId().toString();

        com.reliaquest.api.common.Response<Employee> actual = employeeService.getEmployeeById(searchIdValue);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @DisplayName("find employee by id, error")
    @Test
    void findEmployeeByIdError() throws IOException {
        when(employeeServiceClientMock.get(any())).thenThrow(exceptionMock);

        com.reliaquest.api.common.Response<Object> expected =
                com.reliaquest.api.common.Response.error(EXCEPTION_MESSAGE);

        com.reliaquest.api.common.Response<Employee> actual = employeeService.getEmployeeById("1");

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
    }

    @DisplayName("find employee by id, not found")
    @Test
    void findEmployeeByIdNotFound() throws IOException {
        when(responseMock.body()).thenReturn(null);

        com.reliaquest.api.common.Response<Object> expected = com.reliaquest.api.common.Response.error("0 null");

        com.reliaquest.api.common.Response<Employee> actual = employeeService.getEmployeeById("1");

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("create employee, success")
    @Test
    void createEmployee() throws IOException {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setName(employee.getName());
        employeeRequest.setSalary(employee.getSalary());
        employeeRequest.setAge(employee.getAge());
        employeeRequest.setTitle(employee.getTitle());

        com.reliaquest.api.common.Response<Employee> expected =
                com.reliaquest.api.common.Response.handledWith(employee);
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(expected));
        when(responseMock.isSuccessful()).thenReturn(true);

        com.reliaquest.api.common.Response<Employee> actual = employeeService.createEmployee(employeeRequest);

        assertNotNull(actual);
        assertEquals(employeeRequest.getName(), actual.data().getName());
        assertEquals(employeeRequest.getSalary(), actual.data().getSalary());
        assertEquals(employeeRequest.getAge(), actual.data().getAge());
        assertEquals(employeeRequest.getTitle(), actual.data().getTitle());
        verify(employeeServiceClientMock, times(1)).post(any(), any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("create employee, error")
    @Test
    void createEmployeeError() throws IOException {
        when(responseMock.isSuccessful()).thenReturn(false);

        com.reliaquest.api.common.Response<Employee> actual =
                employeeService.createEmployee(mock(EmployeeRequest.class));

        assertNotNull(actual);
        verify(employeeServiceClientMock, times(1)).post(any(), any());
    }

    @DisplayName("delete employee, not found")
    @Test
    void deleteEmployee() throws IOException {
        when(employeeServiceClientMock.get(any())).thenReturn(responseMock);
        com.reliaquest.api.common.Response<Object> expected = new com.reliaquest.api.common.Response<>(
                null, com.reliaquest.api.common.Response.Status.ERROR, "Employee not found");
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(expected));
        when(responseMock.isSuccessful()).thenReturn(true);
        when(responseMock.body()).thenReturn(responseBodyMock);

        com.reliaquest.api.common.Response<Employee> actual = employeeService.deleteEmployeeById("1");

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(0)).delete(any(), any());
        verify(responseMock, times(1)).body();
        verify(responseBodyMock, times(1)).string();
        verify(responseMock, times(1)).isSuccessful();
    }

    @DisplayName("delete employee, found")
    @Test
    void deleteEmployeeFound() throws IOException {
        when(employeeServiceClientMock.get(any())).thenReturn(responseMock);
        com.reliaquest.api.common.Response<Employee> expected = com.reliaquest.api.common.Response.handledWith(employee);
        when(employeeServiceClientMock.delete(any(), any())).thenReturn(responseMock);
        //when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(expected));
        when(responseMock.isSuccessful()).thenReturn(true);
        //when(responseMock.body()).thenReturn(responseBodyMock);

        com.reliaquest.api.common.Response<Employee> actual = employeeService.deleteEmployeeById(employee.getId());

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(employeeServiceClientMock, times(1)).delete(any(), any());
        verify(responseMock, times(1)).body();
        verify(responseBodyMock, times(1)).string();
        verify(responseMock, times(2)).isSuccessful();
    }

    @DisplayName("find highest salary, 20 employees")
    @Test
    void findHighestSalaryOfEmployees() throws IOException {
        int employeeCount = 20;
        List<Employee> employees = new ArrayList<>(employeeCount);
        for (int i = 1; i <= employeeCount; i++) {
            Integer salary = 50000 + (i * 1000);
            Employee employeeActual = new Employee();
            employeeActual.setSalary(salary);
            employees.add(employeeActual);
        }
        com.reliaquest.api.common.Response<List<Employee>> expected =
                com.reliaquest.api.common.Response.handledWith(employees);
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(expected));
        when(responseMock.body()).thenReturn(responseBodyMock);

        Integer actual = employeeService.getHighestSalaryOfEmployees();

        assertNotNull(actual);
        assertEquals(70000, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("find highest salary, no employees")
    @Test
    void findHighestSalaryOfEmployeesNoneAvailable() throws IOException {
        com.reliaquest.api.common.Response<List<Employee>> employeeResponse =
                com.reliaquest.api.common.Response.handledWith(Collections.emptyList());
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(employeeResponse));
        when(responseMock.body()).thenReturn(responseBodyMock);

        Integer actual = employeeService.getHighestSalaryOfEmployees();

        assertNotNull(actual);
        assertEquals(0, actual);
        verify(employeeServiceClientMock, times(1)).get(any());
        verify(responseMock, times(1)).body();
    }

    @DisplayName("find top ten employees by salary, 20 employees")
    @Test
    void findTopTenEmployeesBySalary() throws IOException {
        String[] names = {
            "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hank", "Ivy", "Jack", "Kate", "Leo", "Mona",
            "Nick", "Oscar", "Paul", "Quinn", "Rose", "Steve", "Tina"
        };
        int employeeCount = 20;
        List<Employee> employees = new ArrayList<>(employeeCount);
        for (int i = 1; i <= employeeCount; i++) {
            Integer salary = 50000 + (i * 1000);
            String name = names[i - 1];
            Employee employee = new Employee();
            employee.setSalary(salary);
            employee.setName(name);
            employees.add(employee);
        }

        com.reliaquest.api.common.Response<List<Employee>> empoloyeesResponse =
                com.reliaquest.api.common.Response.handledWith(employees);
        when(responseBodyMock.string()).thenReturn(objectMapper.writeValueAsString(empoloyeesResponse));
        when(responseMock.body()).thenReturn(responseBodyMock);

        List<String> expected =
                Arrays.asList("Tina", "Steve", "Rose", "Quinn", "Paul", "Oscar", "Nick", "Mona", "Leo", "Kate");

        List<String> actual = employeeService.getTopTenHighestEarningEmployeeNames();

        assertNotNull(actual);
        assertEquals(10, actual.size());
        assertEquals(expected, actual);
    }

    @DisplayName("handle response error")
    @Test
    void handleResponseError() throws IOException {
        when(responseBodyMock.string()).thenReturn("bad string");

        com.reliaquest.api.common.Response<List<Employee>> actual = employeeService.getAllEmployees();

        assertEquals(com.reliaquest.api.common.Response.Status.ERROR, actual.status());
    }
}
