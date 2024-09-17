package com.example.clinic.repository;

import com.example.clinic.model.Person;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends ExtendedJpaRepository<Person, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Person p set p.lastLoginDate = CURRENT_TIMESTAMP where p.id = :id")
    void updateLastLoginDate(@Param("id") Long id);
}