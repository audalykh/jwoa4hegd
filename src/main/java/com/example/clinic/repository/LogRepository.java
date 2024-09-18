package com.example.clinic.repository;

import com.example.clinic.model.Log;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends ExtendedJpaRepository<Log, Long> {
}