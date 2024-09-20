package com.example.clinic.controller;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.exception.PersonAlreadyExistException;
import com.example.clinic.service.PatientService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import static com.example.clinic.security.Auth.ROLE_PATIENT;

@RestController
@Secured(ROLE_DOCTOR)
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public List<PersonDto> getPage(@PageableDefault Pageable pageable) {
        return patientService.getPage(pageable).getContent();
    }

    /**
     * Creates a new patient using the given dto.
     * @throws PersonAlreadyExistException if a patient with the same email already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDto create(@RequestBody @Valid PersonBaseDto dto) {
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

    /**
     * Returns the current Person data; only names and email are returned; all other sensitive data
     * like "id", create/login timestamps are omitted.
     */
    @Secured(ROLE_PATIENT)
    @GetMapping("/current")
    public PersonBaseDto getCurrent(Principal principal) {
        return patientService.getByEmailOrThrow(principal.getName());
    }
}