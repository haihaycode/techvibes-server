package com.haihaycode.techvibesservice.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
    private String message;
    private LocalDateTime timestamp;
    private HttpStatus httpStatus;
    private T data;

    public ResponseWrapper(HttpStatus httpStatus, String message, T data) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.data = data;
        this.httpStatus = httpStatus;
    }



}
