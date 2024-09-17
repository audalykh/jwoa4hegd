package com.example.clinic.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final Long actorId;

    public CustomUsernamePasswordAuthenticationToken(Long actorId, String principal, Object credentials,
                                                     Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.actorId = actorId;
    }
}