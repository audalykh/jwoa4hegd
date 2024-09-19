package com.example.clinic.repository;

import com.example.clinic.model.Doctor;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends BasePersonRepository<Doctor> {

    boolean existsByEmail(String email);
}