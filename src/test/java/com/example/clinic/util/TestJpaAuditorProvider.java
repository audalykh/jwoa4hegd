package com.example.clinic.util;

import com.example.clinic.jpa.DefaultJpaAuditorProvider;
import com.example.clinic.security.Auth;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * TestJpaAuditorProvider is a class that extends DefaultJpaAuditorProvider to provide custom logic for retrieving
 * the auditor ID from the authentication object.
 * As long as in the tests JWT token is not used and actorId can not be fetched from the token, it is needed to
 * get the actorId from the database based on the current username and role.
 */
@RequiredArgsConstructor
public class TestJpaAuditorProvider extends DefaultJpaAuditorProvider {

    private final DbUtil dbUtil;

    @Override
    protected Optional<Long> getAuditorId(Authentication authentication) {
        var result = super.getAuditorId(authentication);
        if (result.isEmpty() && authentication instanceof UsernamePasswordAuthenticationToken token) {
            String type = token.getAuthorities().stream()
                    .filter(authority -> authority.getAuthority().equals(Auth.ROLE_DOCTOR))
                    .map(grantedAuthority -> "D").findFirst().orElse("P");
            return Optional.of(dbUtil.getPersonIdByEmail(authentication.getName(), type));
        }
        return Optional.empty();
    }
}