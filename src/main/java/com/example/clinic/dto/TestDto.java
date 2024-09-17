package com.example.clinic.dto;

import com.example.clinic.model.TestType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDto extends TestBaseDto {

    @NotNull
    private TestType type;

    @NotNull
    private LocalDateTime testDateTime;
}