package com.example.clinic.exception;

import java.io.Serial;

public class PersonAlreadyExistException extends Exception {

    @Serial
    private static final long serialVersionUID = -1948664700632586699L;

    public PersonAlreadyExistException(String message) {
        super(message);
    }
}