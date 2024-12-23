package com.reliaquest.api.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDeleteRequest {
    private String name;
}
