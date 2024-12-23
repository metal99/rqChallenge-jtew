package com.reliaquest.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse<T> {

    private T data;
    private String status;
    private String error;
}
