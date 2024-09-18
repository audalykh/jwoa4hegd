package com.example.clinic;

import com.example.clinic.config.InitDataConfiguration;
import com.example.clinic.dto.JwtAuthenticationDto;
import com.example.clinic.dto.SignInDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.EntityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.clinic.util.TestUtil.asJsonString;
import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthIntegrationTests extends BaseIntegrationTests {

    @Autowired
    private InitDataConfiguration initDataConfiguration;


    @Test
    void shouldSignInAsExistingDoctor() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsDoctor();

        var person = jwtAuthenticationDto.getPerson();
        var admin = initDataConfiguration.getAdminDoctor();

        assertThat(jwtAuthenticationDto.getJwt()).isNotNull();
        assertThat(person.getEmail()).isEqualTo(admin.getEmail());
        assertThat(person.getLastLoginAt()).isNotNull();
        assertThat(person.getCreatedAt()).isNotNull();
    }

    @Test
    public void shouldCreateLogEntryOnSuccessfulDoctorSignIn() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsDoctor();

        // Assert
        var person = jwtAuthenticationDto.getPerson();

        var logs = logService.getAll();
        assertThat(logs).hasSize(1);

        var log = logs.get(0);
        assertThat(log.getActionType()).isEqualTo(ActionType.LOG_IN);
        assertThat(log.getEntityType()).isEqualTo(EntityType.DOCTOR);
        assertThat(log.getEntityId()).isEqualTo(person.getId());
        assertThat(log.getActorId()).isEqualTo(person.getId());
    }

    private JwtAuthenticationDto loginAsDoctor() throws Exception {
        // Arrange
        var admin = initDataConfiguration.getAdminDoctor();
        var signInDto = new SignInDto().setEmail(admin.getEmail()).setPassword(admin.getPassword());

        // Act
        var mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in/doctor")
                        .content(asJsonString(signInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, JwtAuthenticationDto.class);
    }

    @Test
    public void shouldFailOnSignInAttemptWithInvalidDoctorCreds() throws Exception {
        // Arrange
        var signInDto = new SignInDto().setEmail("foo").setPassword("bar");

        // Act & Assert
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in/doctor")
                        .content(asJsonString(signInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains("Bad credentials");
    }

    @Test
    public void shouldSignInAsPatient() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsPatient();

        assertThat(jwtAuthenticationDto.getJwt()).isNotNull();
        assertThat(jwtAuthenticationDto.getPerson().getEmail()).isEqualTo(dummyPatient.getEmail());
    }

    @Test
    public void shouldCreateLogEntryOnSuccessfulPatientSignIn() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsPatient();

        // Assert
        var person = jwtAuthenticationDto.getPerson();
        var logs = logService.getAll();
        assertThat(logs).hasSize(1);

        var log = logs.get(0);
        assertThat(log.getActionType()).isEqualTo(ActionType.LOG_IN);
        assertThat(log.getEntityType()).isEqualTo(EntityType.PATIENT);
        assertThat(log.getEntityId()).isEqualTo(person.getId());
        assertThat(log.getActorId()).isEqualTo(person.getId());
    }

    private JwtAuthenticationDto loginAsPatient() throws Exception {
        // Arrange
        var patient = createPatient();

        var signInDto = new SignInDto().setEmail(patient.getEmail()).setPassword(dummyPatient.getPassword());

        // Act
        var mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in/patient")
                        .content(asJsonString(signInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, JwtAuthenticationDto.class);
    }
}