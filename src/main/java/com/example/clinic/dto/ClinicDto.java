package com.example.clinic.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ClinicDto {

    private String name;
    private String email;
    private String phone;
    private Integer fromHour;
    private Integer toHour;
    private LogoDto logo;
}