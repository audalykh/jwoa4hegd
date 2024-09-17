package com.example.clinic.controller;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.AppointmentRequestDto;
import com.example.clinic.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.example.clinic.security.Auth.ROLE_DOCTOR;

@RestController
@Secured(ROLE_DOCTOR)
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public Page<AppointmentDto> getPage(@PageableDefault Pageable pageable) {
        return appointmentService.getPage(pageable);
    }

    @PostMapping
    public AppointmentDto create(@RequestBody @Valid AppointmentRequestDto dto) {
        return appointmentService.create(dto);
    }

    @GetMapping("/{id}")
    public AppointmentDto get(@PathVariable Long id) {
        return appointmentService.getById(id);
    }

    @PutMapping("/{id}")
    public AppointmentDto update(@PathVariable Long id, @RequestBody @Valid AppointmentRequestDto dto) {
        return appointmentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        appointmentService.delete(id);
    }
}