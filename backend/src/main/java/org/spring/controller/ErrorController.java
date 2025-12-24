package org.spring.controller;

import org.spring.dto.ErrorDto;
import org.spring.exc.AuthCommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(AuthCommonException.class)
    public ResponseEntity<ErrorDto> handleUserAlreadyExistException(AuthCommonException exc) {

        var errorDto = new ErrorDto(exc.getCode(), exc.getMessage());

        return ResponseEntity.status(exc.getCode()).body(errorDto);
    }
}
