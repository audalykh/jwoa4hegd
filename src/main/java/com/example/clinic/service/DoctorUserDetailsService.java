package com.example.clinic.service;

import com.example.clinic.dto.PersonDetails;
import com.example.clinic.model.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorUserDetailsService implements UserDetailsService {

    private final DoctorService doctorService;

    /**
     * Loads a doctor for authentication based on the given username.
     *
     * @param username the username/email of the doctor to be loaded
     * @return the UserDetails object for the loaded user
     * @throws UsernameNotFoundException if the user with the given username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Doctor doctor = doctorService.findByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException(username));
        return new PersonDetails(doctor);
    }
}