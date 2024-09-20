package com.example.clinic.service;

import com.example.clinic.dto.TestCreateDto;
import com.example.clinic.dto.TestDto;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.mapper.TestMapper;
import com.example.clinic.model.Patient;
import com.example.clinic.repository.TestRepository;
import com.example.clinic.repository.spec.TestSpec;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TestMapper testMapper;

    public TestDto create(TestCreateDto dto) {
        var entity = testRepository.save(testMapper.toEntity(dto));
        return testMapper.toDto(entity);
    }

    public void delete(Long id) {
        testRepository.deleteById(id);
    }

    public TestDto update(Long id, TestDto dto) {
        var test = testRepository.findById(id)
                .orElseThrow(() ->  new DomainObjectNotFoundException("Test not found; id: " + id));
        var updated = testRepository.save(testMapper.toEntity(dto, test));
        return testMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    public List<TestDto> getPatientTests(Patient patient) {
        var specification = TestSpec.patientEq(patient);
        return testRepository.findAll(specification, Sort.by("testDateTime"))
                .stream().map(testMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TestDto> getAllTests() {
        return testRepository.findAll().stream().map(testMapper::toDto).toList();
    }
}