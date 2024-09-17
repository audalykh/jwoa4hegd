package com.example.clinic.service;

import com.example.clinic.dto.PersonDetails;
import com.example.clinic.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientUserDetailsService implements UserDetailsService {

    private final PatientService patientService;

    /**
     * Loads a patient for authentication based on the given username.
     *
     * @param username the username/email of the patient to be loaded
     * @return the UserDetails object for the loaded user
     * @throws UsernameNotFoundException if the user with the given username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Patient patient = patientService.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException(username));
        return new PersonDetails(patient);
    }
}