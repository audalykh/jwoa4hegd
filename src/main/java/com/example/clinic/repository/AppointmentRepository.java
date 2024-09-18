package com.example.clinic.repository;

import com.example.clinic.model.Appointment;
import com.example.clinic.model.AppointmentStatus;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends ExtendedJpaRepository<Appointment, Long> {

    List<Appointment> findAppointmentsByStatusNot(AppointmentStatus status);
}