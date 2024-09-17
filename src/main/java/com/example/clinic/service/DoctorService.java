package com.example.clinic.service;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.exception.PersonAlreadyExistException;
import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Doctor;
import com.example.clinic.repository.DoctorRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PersonMapper personMapper;

    @Transactional
    public void create(PersonBaseDto dto) throws PersonAlreadyExistException {
        if (doctorRepository.existsByEmail(dto.getEmail())) {
            throw new PersonAlreadyExistException("Doctor with email " + dto.getEmail() + " already exists");
        } else {
            doctorRepository.save(personMapper.toDoctorEntity(dto));
        }
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
}