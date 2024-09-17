package com.example.clinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class ClinicAlreadyExistException extends HttpClientErrorException {

    public ClinicAlreadyExistException() {
        super(HttpStatus.BAD_REQUEST, "Clinic already exist");
    }
}