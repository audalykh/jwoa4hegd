package com.example.clinic.dto;

import com.example.clinic.model.TestResult;
import com.example.clinic.model.TestType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TestDto {

    private Long id;

    @NotNull
    private TestType type;

    @NotNull
    private LocalDateTime testDateTime;

    private TestResult result;

    private LocalDateTime resultDateTime;
}