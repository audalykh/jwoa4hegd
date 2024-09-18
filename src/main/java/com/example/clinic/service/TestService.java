package com.example.clinic.service;

import com.example.clinic.dto.TestBaseDto;
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

    @Transactional(readOnly = true)
    public List<TestDto> getPatientTests(Patient patient) {
        var specification = TestSpec.patientEq(patient);
        return testRepository.findAll(specification, Sort.by("testDateTime"))
                .stream().map(testMapper::toDto).toList();
    }
}