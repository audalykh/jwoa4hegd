package com.example.clinic.dto;

import com.example.clinic.model.Person;
import com.example.clinic.security.Role;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class PersonDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = -5215557740979852595L;

    private final String username;
    private final String password;
    private final Long id;
    private final String role;

    public PersonDetails(Person person) {
        this.username = person.getEmail();
        this.password = person.getPassword();
        this.id = person.getId();
        this.role = Role.fromPerson(person).getRoleName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
}