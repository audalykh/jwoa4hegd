package com.example.clinic.controller;

import com.example.clinic.dto.JwtAuthenticationDto;
import com.example.clinic.dto.SignInDto;
import com.example.clinic.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.clinic.security.Role.DOCTOR;
import static com.example.clinic.security.Role.PATIENT;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-in/doctor")
    public JwtAuthenticationDto doctorSignIn(@RequestBody @Valid SignInDto request) {
        return authenticationService.signIn(request, DOCTOR);
    }

    @PostMapping("/sign-in/patient")
    public JwtAuthenticationDto patientSignIn(@RequestBody @Valid SignInDto request) {
        return authenticationService.signIn(request, PATIENT);
    }
}