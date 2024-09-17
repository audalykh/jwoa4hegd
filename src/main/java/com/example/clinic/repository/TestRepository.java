package com.example.clinic.repository;

import com.example.clinic.model.Test;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends ExtendedJpaRepository<Test, Long> {
}