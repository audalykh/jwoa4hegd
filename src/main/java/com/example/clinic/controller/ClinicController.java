package com.example.clinic.controller;

import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.dto.ClinicDto;
import com.example.clinic.service.ClinicService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.example.clinic.security.Auth.ROLE_DOCTOR;
import static com.example.clinic.security.Auth.ROLE_PATIENT;

@RestController
@Secured(ROLE_DOCTOR)
@RequiredArgsConstructor
@RequestMapping("/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    @PostMapping
    public ClinicDto create(@Valid ClinicBaseDto clinicDto,
                            @RequestPart MultipartFile logo) throws IOException {
        return clinicService.create(clinicDto, logo);
    }

    /**
     * This API returns the clinic information, if the clinic exists.
     * Available to both doctors and patients.
     */
    @GetMapping
    @Secured({ROLE_DOCTOR, ROLE_PATIENT})
    public ClinicDto get() {
        return clinicService.getClinic();
    }

    @PutMapping
    public ClinicDto update(@Valid ClinicBaseDto clinicDto, @RequestPart MultipartFile logo) throws IOException {
        return clinicService.update(clinicDto, logo);
    }
}