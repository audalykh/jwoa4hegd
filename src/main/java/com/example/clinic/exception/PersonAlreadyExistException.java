package com.example.clinic.exception;

public class PersonAlreadyExistException extends Exception {

    public PersonAlreadyExistException(String message) {
        super(message);
    }
}