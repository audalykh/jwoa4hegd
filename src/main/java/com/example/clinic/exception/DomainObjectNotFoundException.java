package com.example.clinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class DomainObjectNotFoundException extends HttpClientErrorException {

    public DomainObjectNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}