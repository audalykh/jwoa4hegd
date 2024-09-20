package com.example.clinic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TestCreateDto extends TestDto {

    @NotNull
    private Long appointmentId;
}