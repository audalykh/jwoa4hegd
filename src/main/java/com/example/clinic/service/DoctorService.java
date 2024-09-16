package com.example.clinic.service;

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

    @Transactional
    public void create(Doctor doctor) {
        if (doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new IllegalArgumentException("Doctor with email " + doctor.getEmail() + " already exists");
        } else {
            doctorRepository.save(doctor);
        }
    }

    @Nullable
    @Transactional(readOnly = true)
    public Doctor findByEmail(String email) {
        return doctorRepository.findDoctorByEmail(email);
    }
}