package com.example.clinic.exception;

import com.example.clinic.model.AppointmentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Represents an exception that is thrown when an invalid appointment status transition is attempted.
 */
public class InvalidAppointmentStatusException extends HttpClientErrorException {

    public InvalidAppointmentStatusException(AppointmentStatus oldStatus, AppointmentStatus newStatus) {
        super(HttpStatus.BAD_REQUEST, "Invalid appointment status transition: " + oldStatus + " -> " + newStatus);
    }
}