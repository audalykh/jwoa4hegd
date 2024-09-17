package com.example.clinic.repository;

import com.example.clinic.model.Appointment;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends ExtendedJpaRepository<Appointment, Long> {
}