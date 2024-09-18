package com.example.clinic.service;

import com.example.clinic.model.Log;
import com.example.clinic.repository.LogRepository;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional
    public void save(Log log) {
        logRepository.save(log);
    }

    @Transactional
    public void saveAll(Collection<Log> logs) {
        logRepository.saveAll(logs);
    }

    @Transactional(readOnly = true)
    public List<Log> getAll() {
        return logRepository.findAll();
    }
}