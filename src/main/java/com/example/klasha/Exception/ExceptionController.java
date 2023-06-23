package com.example.klasha.Exception;

import com.example.klasha.Exception.response.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
        @ExceptionHandler(CustomNotFoundException.class)
        public ResponseEntity<?> CustomerNotFoundException(CustomNotFoundException ex) {
            return new ResponseEntity<>(new ExceptionResponse(true,ex.getMessage()), HttpStatus.NOT_FOUND);
        }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> BadRequest(BadRequestException ex) {
        return new ResponseEntity<>(new ExceptionResponse(true,ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
