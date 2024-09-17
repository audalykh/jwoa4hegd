package com.example.clinic.dto;

import com.example.clinic.model.TestResult;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestBaseDto {

    private TestResult result;

    private LocalDateTime resultDateTime;
}