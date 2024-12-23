package com.reliaquest.api.common;

public class Constants {

    // path constants
    public static final String BASE_URL =
            "http://localhost:8112/api/v1"; // TODO add to application properties for production/test env variables

    public static final String PATH_EMPLOYEE = "/employee";
    public static final String PATH_SEARCH = "/search/{searchString}";
    public static final String PATH_ID = "/{id}";
    public static final String PATH_HIGHEST_SALARY = "/highestSalary";
    public static final String PATH_TOP_TEN_HIGHEST_EARNING_EMPLOYEE_NAMES = "/topTenHighestEarningEmployeeNames";

    // media type constants
    public static final String APPLICATION_JSON = "application/json";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
