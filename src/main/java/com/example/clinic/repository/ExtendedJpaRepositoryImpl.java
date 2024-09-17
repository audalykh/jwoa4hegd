package com.example.clinic.repository;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public class ExtendedJpaRepositoryImpl<T, V extends Serializable>
        extends SimpleJpaRepository<T, V>
        implements ExtendedJpaRepository<T, V> {

    private final EntityManager em;

    public ExtendedJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
    }

    @Override
    @Transactional
    public T saveAndRefresh(T entity) {
        em.refresh(save(entity));
        return entity;
    }
}