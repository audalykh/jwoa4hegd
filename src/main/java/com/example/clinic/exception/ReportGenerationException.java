package com.example.clinic.exception;

public class ReportGenerationException extends RuntimeException {

    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}