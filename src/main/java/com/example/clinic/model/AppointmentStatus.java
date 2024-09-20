package com.example.clinic.model;

public enum AppointmentStatus {
    NEW,
    IN_PROGRESS,
    CLOSED;

    /**
     * Determines if the given appointment status is after or the same as the current status.
     *
     * @param status the appointment status to compare
     * @return true if the given status is after or the same as the current status, false otherwise
     */
    public boolean isAfterOrSame(AppointmentStatus status) {
        return compareTo(status) >= 0;
    }
}