package com.example.clinic.repository;

import com.example.clinic.model.Person;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BasePersonRepository<T extends Person> extends ExtendedJpaRepository<T, Long> {

    Optional<T> findByEmail(String email);
}