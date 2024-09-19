package com.example.clinic;

import com.example.clinic.dto.AppointmentCreateDto;
import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.AppointmentStatus;
import com.example.clinic.model.EntityType;
import com.example.clinic.model.Patient;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.clinic.BaseTests.ADMIN_EMAIL;
import static com.example.clinic.BaseTests.DOCTOR;
import static com.example.clinic.util.TestUtil.asJsonString;
import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
public class AppointmentTests extends BaseTests {

    private Patient patient;

    @BeforeEach
    public void setUp() {
        patient = domainUtil.createPatient(dummyPatient);
    }

    @Test
    public void shouldGetPaginatedAndSortedDoctors() throws Exception {
        // Arrange
        createAppointment(new AppointmentCreateDto(patient.getId()));

        // Act & Assert
        assertThat(fetchAppointments(0, 10)).hasSize(1);
        assertThat(fetchAppointments(1, 1)).isEmpty();
    }

    @Test
    public void shouldCreateNewAppointment() throws Exception {

        // Arrange
        var dto = new AppointmentCreateDto(patient.getId());

        // Act
        var appointment = createAppointment(dto);

        // Assert
        assertThat(appointment)
                .matches(a -> a.getPatient().getId() == patient.getId())
                .matches(a -> a.getCreatedBy().getId() == adminDoctor.getId())
                .matches(a -> a.getStatus() == AppointmentStatus.NEW)
                .matches(a -> a.getRevisitDateTime() == null)
                .matches(a -> a.getTests().isEmpty());

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.CREATE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.APPOINTMENT)
                .matches(l -> l.getEntityId().equals(appointment.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldRejectInvalidRevisitDateTime() throws Exception {

        // Arrange
        var invalidRevisitDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusHours(1); // 01:00 AM
        var dto = new AppointmentCreateDto(patient.getId()).setRevisitDateTime(invalidRevisitDateTime);

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/appointments")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        // Assert
        assertThat(mvcResult.getResponse().getContentAsString())
                .contains("Invalid revisit time");
    }

    private List<AppointmentDto> fetchAppointments(int page, int size) throws Exception {
        var mvcResult = mockMvc.perform(get("/api/appointments")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, new TypeReference<>() {
        });
    }

    private AppointmentDto createAppointment(AppointmentCreateDto dto) throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/appointments")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, AppointmentDto.class);
    }
}