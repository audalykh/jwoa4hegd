package com.example.clinic.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class PersonDto extends PersonBaseDto {

    private long id;

    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;
}