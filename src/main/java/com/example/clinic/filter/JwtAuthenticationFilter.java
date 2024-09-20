package com.example.clinic.filter;

import com.example.clinic.security.CustomUsernamePasswordAuthenticationToken;
import com.example.clinic.service.DoctorService;
import com.example.clinic.service.JwtService;
import com.example.clinic.service.PatientService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            extractJwtToken(authHeader, request);
            filterChain.doFilter(request, response);
        } catch (JwtException jwtException) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "JTW Token has expired or invalid");
        }
    }

    private void extractJwtToken(String authHeader, HttpServletRequest request) {
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        var username = jwtService.extractUserName(jwt);

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            var role = jwtService.extractRole(jwt);

            var person = switch (role) {
                case DOCTOR -> doctorService.findByEmailOrThrow(username);
                case PATIENT -> patientService.findByEmailOrThrow(username);
            };

            if (jwtService.isTokenValid(jwt, person)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new CustomUsernamePasswordAuthenticationToken(
                        person.getId(),
                        person.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(role.getRoleName()))
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
    }
}