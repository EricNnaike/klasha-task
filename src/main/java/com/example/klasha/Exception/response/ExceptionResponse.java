package com.example.klasha.Exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Data
public class ExceptionResponse {
    private boolean error;
    private String message;
}
