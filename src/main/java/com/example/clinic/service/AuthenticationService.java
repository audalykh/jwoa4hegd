package com.example.clinic.service;

import com.example.clinic.dto.JwtAuthenticationDto;
import com.example.clinic.dto.SignInDto;
import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Person;
import com.example.clinic.model.Role;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PersonMapper personMapper;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PersonService personService;

    /**
     * User authentication
     * @throws AuthenticationException if authentication fails
     */
    @Transactional
    public JwtAuthenticationDto signIn(SignInDto signInDto, Role role) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInDto.getEmail(),
                signInDto.getPassword(),
                List.of(new SimpleGrantedAuthority(role.name()))
        ));

        Person person = switch (role) {
            case DOCTOR -> doctorService.findByEmailOrThrow(signInDto.getEmail());
            case PATIENT -> patientService.findByEmailOrThrow(signInDto.getEmail());
        };

        var jwt = jwtService.generateToken(person);
        var dto = personMapper.toDto(personService.updateLastLogin(person.getId()));

        return new JwtAuthenticationDto(jwt, dto);
    }
}