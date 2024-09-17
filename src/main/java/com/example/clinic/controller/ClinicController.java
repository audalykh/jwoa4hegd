package com.example.clinic.controller;

import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.model.Clinic;
import com.example.clinic.service.ClinicService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.example.clinic.security.Auth.ROLE_DOCTOR;

@RestController
@Secured(ROLE_DOCTOR)
@RequiredArgsConstructor
@RequestMapping("/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    @PostMapping
    public Clinic create(@Valid ClinicBaseDto clinicDto,
                               @RequestPart MultipartFile logo) throws IOException {
        return clinicService.create(clinicDto, logo.getBytes());
    }

    @GetMapping("/{id}")
    public Clinic get(@PathVariable Long id) {
        return clinicService.getById(id);
    }

    @PutMapping("/{id}")
    public Clinic update(@PathVariable Long id, @Valid ClinicBaseDto clinicDto,
                               @RequestPart MultipartFile logo) throws IOException {
        return clinicService.update(id, clinicDto, logo.getBytes());
    }
}