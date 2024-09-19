package com.example.clinic.util;

import com.example.clinic.jpa.DefaultJpaAuditorProvider;
import com.example.clinic.security.Auth;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

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