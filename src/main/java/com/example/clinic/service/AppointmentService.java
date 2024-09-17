package com.example.clinic.service;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.AppointmentRequestDto;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.mapper.AppointmentMapper;
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
    public AppointmentDto create(AppointmentRequestDto dto) {
        var entity = appointmentMapper.toEntity(dto).setStatus(AppointmentStatus.NEW);
        entity = appointmentRepository.saveAndRefresh(entity);
        return appointmentMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public AppointmentDto getById(Long id) {
        return appointmentRepository.findById(id).map(appointmentMapper::toDto)
                .orElseThrow(() -> new DomainObjectNotFoundException("Appointment not found for id: " + id));
    }

    @Transactional
    public AppointmentDto update(Long id, AppointmentRequestDto dto) {
        // TODO
        return null;
    }

    @Transactional
    public void delete(Long id) {
        appointmentRepository.deleteById(id);
    }
}