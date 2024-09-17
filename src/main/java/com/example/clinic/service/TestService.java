package com.example.clinic.service;

import com.example.clinic.dto.TestBaseDto;
import com.example.clinic.dto.TestCreateDto;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.mapper.TestMapper;
import com.example.clinic.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;

    public void create(TestCreateDto dto) {
        testRepository.save(testMapper.toEntity(dto));
    }

    public void delete(Long id) {
        testRepository.deleteById(id);
    }

    public void update(Long id, TestBaseDto dto) {
        var test = testRepository.findById(id)
                .orElseThrow(() ->  new DomainObjectNotFoundException("Test not found; id: " + id));
        testRepository.save(testMapper.toEntity(dto, test));
    }
}