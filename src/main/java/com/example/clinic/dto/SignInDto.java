package com.example.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SignInDto {

    @Size(max = 50)
    @NotBlank(message = "Email can not be blank")
    private String email;

    @Size(max = 64)
    @NotBlank(message = "Password can not be blank")
    private String password;
}