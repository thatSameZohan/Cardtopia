package org.spring.web;

import org.spring.dto.ErrorDto;
import org.spring.exc.UserCommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(UserCommonException.class)
    public ResponseEntity<ErrorDto> handleUserCommonException(UserCommonException exc) {

        var errorDto= new ErrorDto(exc.getCode(),exc.getMessage());

        return ResponseEntity.status(400).body(errorDto);
    }
}
