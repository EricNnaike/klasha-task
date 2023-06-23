package com.example.klasha.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BaseResponse {
    private boolean error;
    private String msg;
    private List<Map<String,?>> data;
}
