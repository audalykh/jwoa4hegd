package com.example.clinic.service;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.exception.PersonAlreadyExistException;
import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Patient;
import com.example.clinic.repository.PatientRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("patientService")
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PersonMapper personMapper;

    @Transactional(readOnly = true)
    public Page<Patient> getPage(Pageable pageable) {
        return patientRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastName", "firstName")));
    }

    @Transactional
    public PersonDto create(PersonBaseDto dto) throws PersonAlreadyExistException {
        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new PersonAlreadyExistException("Patient with email " + dto.getEmail() + " already exists");
        } else {
            // Save and refresh the entity to get the created date
            var patient = patientRepository.saveAndRefresh(personMapper.toPatientEntity(dto));
            return personMapper.toDto(patient);
        }
    }

    /**
     * Creates a new patient or throws an exception if the patient already exists.
     */
    @Transactional
    public Patient createOrThrow(PersonBaseDto dto) {
        return patientRepository.save(personMapper.toPatientEntity(dto));
    }

    @Transactional(readOnly = true)
    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findPatientByEmail(email);
    }

    @Transactional(readOnly = true)
    public Patient findByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Patient not found for email: " + email));
    }

    @Transactional(readOnly = true)
    public Patient findByIdOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new DomainObjectNotFoundException("Patient not found for id: " + id));
    }

    @Transactional(readOnly = true)
    public PersonBaseDto getDtoByEmailOrThrow(String email) {
        return findByEmail(email)
                .map(personMapper::toBaseDto)
                .orElseThrow(() -> new UsernameNotFoundException("Patient not found for email: " + email));
    }

    @Transactional(readOnly = true)
    public PersonDto getById(Long id) {
        return patientRepository.findById(id)
                .map(personMapper::toDto)
                .orElseThrow(() -> new DomainObjectNotFoundException("Patient not found for id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        patientRepository.deleteById(id);
    }

    @Transactional
    public PersonDto update(Long id, PersonBaseDto dto) {
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new DomainObjectNotFoundException("Patient not found for id: " + id));
        return personMapper.toDto(personMapper.toEntity(dto, patient));
    }
}