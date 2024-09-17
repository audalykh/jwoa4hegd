package com.example.clinic.model;

public enum AppointmentStatus {
    NEW,
    IN_PROGRESS,
    CLOSED;

    public boolean isAfterOrSame(AppointmentStatus status) {
        return compareTo(status) >= 0;
    }
}