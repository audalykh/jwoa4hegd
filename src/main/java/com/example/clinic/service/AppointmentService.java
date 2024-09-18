package com.example.clinic.service;

import com.example.clinic.dto.AppointmentCreateDto;
import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.AppointmentRequestDto;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.exception.InvalidAppointmentStatusException;
import com.example.clinic.mapper.AppointmentMapper;
import com.example.clinic.model.Appointment;
import com.example.clinic.model.AppointmentStatus;
import com.example.clinic.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Transactional(readOnly = true)
    public Page<AppointmentDto> getPage(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(appointmentMapper::toDto);
    }

    @Transactional
    public AppointmentDto create(AppointmentCreateDto dto) {
        var entity = appointmentMapper.toEntity(dto).setStatus(AppointmentStatus.NEW);
        return appointmentMapper.toDto(appointmentRepository.saveAndRefresh(entity));
    }

    @Transactional(readOnly = true)
    public AppointmentDto getById(Long id) {
        return appointmentRepository.findById(id).map(appointmentMapper::toDto)
                .orElseThrow(() -> new DomainObjectNotFoundException("Appointment not found for id: " + id));
    }

    @Transactional
    public AppointmentDto update(Long id, AppointmentRequestDto dto) {
        var entity = appointmentRepository.findById(id)
                .map(appointment -> validateStatus(dto, appointment))
                .map(appointment -> appointmentMapper.toEntity(dto, appointment))
                .orElseThrow(() -> new DomainObjectNotFoundException("Appointment not found for id: " + id));
        return appointmentMapper.toDto(appointmentRepository.saveAndFlush(entity));
    }

    /**
     * Validates the status of an appointment based on the provided appointment request DTO. The new appointment status
     * should be either the same or after the current status of the appointment.
     * @throws InvalidAppointmentStatusException if the new status is not valid.
     */
    private Appointment validateStatus(AppointmentRequestDto dto, Appointment appointment) {
        var newStatus = dto.getStatus();
        if (newStatus == null || newStatus.isAfterOrSame(appointment.getStatus())) {
            return appointment;
        }
        throw new InvalidAppointmentStatusException(appointment.getStatus(), newStatus);
    }

    @Transactional
    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }

    /**
     * Closes all appointments that are not already closed.
     *
     * @return The number of appointments closed.
     */
    public int closeAppointments() {
        var notClosedAppointments = appointmentRepository.findAppointmentsByStatusNot(AppointmentStatus.CLOSED);
        notClosedAppointments.forEach(appointment -> appointment.setStatus(AppointmentStatus.CLOSED));
        appointmentRepository.saveAll(notClosedAppointments);

        return notClosedAppointments.size();
    }
}