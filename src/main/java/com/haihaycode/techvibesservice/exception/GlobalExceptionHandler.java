package com.haihaycode.techvibesservice.exception;

import com.haihaycode.techvibesservice.model.ResponseWrapper;
import com.haihaycode.techvibesservice.model.auth.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleUsernameNotFound(UsernameNotFoundException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleInvalidInput(InvalidInputException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleUsernameAlreadyExists(UserAlreadyExistsException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.CONFLICT, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleRoleNotFound(RoleNotFoundException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMessage.append(fieldName).append(" : ").append(message).append(" ; ");
        });
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.BAD_REQUEST, errorMessage.toString(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ResponseWrapper<Void>> handleInvalidTokenException(InvalidTokenException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.UNAUTHORIZED, " Access denied : " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResponseWrapper<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.FORBIDDEN, "Access denied: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }


}
