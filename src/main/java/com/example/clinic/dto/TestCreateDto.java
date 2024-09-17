package com.example.clinic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCreateDto extends TestDto {

    @NotNull
    private Long appointmentId;
}