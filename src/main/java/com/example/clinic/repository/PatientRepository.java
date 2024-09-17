package com.example.clinic.repository;

import com.example.clinic.model.Patient;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends CustomRepository<Patient, Long> {

    Optional<Patient> findPatientByEmail(String email);

    boolean existsByEmail(String email);
}