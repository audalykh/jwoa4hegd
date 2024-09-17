package com.example.clinic.repository;

import com.example.clinic.model.Clinic;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepository extends ExtendedJpaRepository<Clinic, Long> {
}