package com.example.clinic.service;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Doctor;
import com.example.clinic.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PersonMapper personMapper;

    @Transactional
    public void create(PersonBaseDto dto) {
        if (doctorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Doctor with email " + dto.getEmail() + " already exists");
        } else {
            doctorRepository.save(personMapper.toDoctorEntity(dto));
        }
    }

    @Nullable
    @Transactional(readOnly = true)
    public Doctor findByEmail(String email) {
        return doctorRepository.findDoctorByEmail(email);
    }
}