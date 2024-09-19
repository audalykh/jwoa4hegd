package com.example.clinic.service;

import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Person;
import com.example.clinic.repository.BasePersonRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public abstract class BasePersonService<T extends Person, R extends BasePersonRepository<T>> {

    protected final R repository;
    protected final PersonMapper personMapper;

    @Transactional(readOnly = true)
    public Page<T> getPage(Pageable pageable) {
        var sort = Sort.by("lastName", "firstName");
        Pageable pageRequest = pageable.isUnpaged() ?
                Pageable.unpaged(sort) :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return repository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    public Optional<T> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}