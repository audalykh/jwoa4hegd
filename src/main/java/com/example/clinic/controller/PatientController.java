package com.example.clinic.controller;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.exception.PersonAlreadyExistException;
import com.example.clinic.model.Patient;
import com.example.clinic.service.PatientService;
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
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public Page<Patient> getPage(@PageableDefault Pageable pageable) {
        return patientService.getPage(pageable);
    }

    @PostMapping
    public PersonDto create(@RequestBody @Valid PersonBaseDto dto) throws PersonAlreadyExistException {
        return patientService.create(dto);
    }

    @GetMapping("/{id}")
    public PersonDto get(@PathVariable Long id) {
        return patientService.getById(id);
    }

    @PutMapping("/{id}")
    public PersonDto update(@PathVariable Long id, @RequestBody @Valid PersonBaseDto dto) {
        return patientService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}