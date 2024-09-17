package com.example.clinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class PersonAlreadyExistException extends HttpClientErrorException {

    public PersonAlreadyExistException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}