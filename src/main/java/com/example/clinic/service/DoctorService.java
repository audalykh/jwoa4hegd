package com.example.clinic.service;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.exception.PersonAlreadyExistException;
import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Doctor;
import com.example.clinic.repository.DoctorRepository;
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
@Service("doctorService")
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PersonMapper personMapper;

    @Transactional(readOnly = true)
    public Page<Doctor> getPage(Pageable pageable) {
        return doctorRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("lastName", "firstName")));
    }

    @Transactional
    public PersonDto create(PersonBaseDto dto) throws PersonAlreadyExistException {
        if (doctorRepository.existsByEmail(dto.getEmail())) {
            throw new PersonAlreadyExistException("Doctor with email " + dto.getEmail() + " already exists");
        } else {
            // Save and refresh the entity to get the created date
            var doctor = doctorRepository.saveAndRefresh(personMapper.toDoctorEntity(dto));
            return personMapper.toDto(doctor);
        }
    }

    /**
     * Creates a new doctor or throws an exception if the doctor already exists.
     */
    @Transactional
    public void createOrThrow(PersonBaseDto dto) {
        doctorRepository.save(personMapper.toDoctorEntity(dto));
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> findByEmail(String email) {
        return doctorRepository.findDoctorByEmail(email);
    }

    @Transactional(readOnly = true)
    public Doctor findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Doctor not found for email: " + email)
        );
    }

    @Transactional(readOnly = true)
    public PersonDto getById(Long id) {
        return doctorRepository.findById(id)
                .map(personMapper::toDto)
                .orElseThrow(() -> new DomainObjectNotFoundException("Doctor not found for id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }

    @Transactional
    public PersonDto update(Long id, PersonBaseDto dto) {
        var doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DomainObjectNotFoundException("Doctor not found for id: " + id));
        return personMapper.toDto(personMapper.toEntity(dto, doctor));
    }
}