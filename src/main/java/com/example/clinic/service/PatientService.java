package com.example.clinic.service;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.exception.PersonAlreadyExistException;
import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Patient;
import com.example.clinic.repository.PatientRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PersonMapper personMapper;

    @Transactional
    public void create(PersonBaseDto dto) throws PersonAlreadyExistException {
        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new PersonAlreadyExistException("Patient with email " + dto.getEmail() + " already exists");
        } else {
            patientRepository.save(personMapper.toPatientEntity(dto));
        }
    }

    @Transactional(readOnly = true)
    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findPatientByEmail(email);
    }

    @Transactional(readOnly = true)
    public Patient findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Patient not found for email: " + email)
        );
    }
}