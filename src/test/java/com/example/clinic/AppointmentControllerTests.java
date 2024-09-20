package com.example.clinic;

import com.example.clinic.dto.AppointmentCreateDto;
import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.AppointmentRequestDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.AppointmentStatus;
import com.example.clinic.model.EntityType;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.clinic.BaseControllerTests.ADMIN_EMAIL;
import static com.example.clinic.BaseControllerTests.DOCTOR;
import static com.example.clinic.util.TestUtil.asJsonString;
import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
public class AppointmentControllerTests extends BaseControllerTests {

    private AppointmentDto appointment;

    @BeforeEach
    public void setUp() throws Exception {
        patient = domainUtil.createPatient(dummyPatient);
        appointment = createAppointment(new AppointmentCreateDto(patient.getId()));
    }

    @Test
    public void shouldLogAppointmentCreation() {

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.CREATE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.APPOINTMENT)
                .matches(l -> l.getEntityId().equals(appointment.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldGetPaginatedAndSortedDoctors() throws Exception {
        // Act & Assert
        assertThat(fetchAppointments(0, 10)).hasSize(1);
        assertThat(fetchAppointments(1, 1)).isEmpty();
    }

    @Test
    public void shouldCreateNewAppointment() throws Exception {

        // Arrange
        var dto = new AppointmentCreateDto(patient.getId());

        // Act
        var appointmentDto = createAppointment(dto);

        // Assert
        assertThat(appointmentDto)
                .matches(a -> a.getPatient().getId() == patient.getId())
                .matches(a -> a.getCreatedBy().getId() == adminDoctor.getId())
                .matches(a -> a.getStatus() == AppointmentStatus.NEW)
                .matches(a -> a.getRevisitDateTime() == null)
                .matches(a -> a.getTests().isEmpty());
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
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Invalid revisit time");
    }

    @Test
    public void shouldGetAppointmentById() throws Exception {

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/appointments/" + appointment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        var appointment = fromJsonString(mvcResult, AppointmentDto.class);

        // Assert
        assertThat(appointment)
                .matches(a -> a.getPatient().getId() == patient.getId())
                .matches(a -> a.getCreatedBy().getId() == adminDoctor.getId())
                .matches(a -> a.getStatus() == AppointmentStatus.NEW)
                .matches(a -> a.getRevisitDateTime() == null)
                .matches(a -> a.getTests().isEmpty());
    }

    @Test
    public void shouldUpdateAppointment() throws Exception {

        // Arrange
        var revisitDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusHours(12);
        var dto = new AppointmentRequestDto(AppointmentStatus.IN_PROGRESS, revisitDate);

        // Act
        var appointmentDto = updateAppointment(dto, appointment.getId());

        // Assert
        assertThat(appointmentDto)
                .matches(a -> a.getPatient().getId() == patient.getId())
                .matches(a -> a.getCreatedBy().getId() == adminDoctor.getId())
                .matches(a -> a.getStatus() == AppointmentStatus.IN_PROGRESS)
                .matches(a -> a.getRevisitDateTime().isEqual(revisitDate));

        // Assert log entry has been updated
        var log = domainUtil.getLogByType(ActionType.UPDATE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.APPOINTMENT)
                .matches(l -> l.getEntityId().equals(appointment.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldForbidMovingAppointmentBackToNewFromInProgress() throws Exception {

        // Arrange
        var dto = new AppointmentRequestDto().setStatus(AppointmentStatus.IN_PROGRESS);
        var appointmentDto = updateAppointment(dto, appointment.getId());
        assertThat(domainUtil.getAllLogs()).hasSize(2);

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/appointments/" + appointmentDto.getId())
                        .content(asJsonString(dto.setStatus(AppointmentStatus.NEW)))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        assertThat(mvcResult.getResponse().getContentAsString())
                .contains("Invalid appointment status transition: IN_PROGRESS -> NEW");

        // no new logs have been created
        assertThat(domainUtil.getAllLogs()).hasSize(2);
    }

    @Test
    public void shouldMarkAppointmentAsDeleted() throws Exception {

        // Arrange
        assertThat(domainUtil.getAllAppointments()).hasSize(1);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/appointments/" + appointment.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        // Assert
        assertThat(domainUtil.getAllAppointments()).hasSize(0);

        var deletedAppointments = dbUtil.doSelect("select * from appointment where id = " + appointment.getId());
        assertThat(deletedAppointments).hasSize(1).matches(d -> d.get(0).get("DELETED").equals(true));

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.DELETE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.APPOINTMENT)
                .matches(l -> l.getEntityId().equals(appointment.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldDeleteAppointmentAlongWithOwningPatient() throws Exception {

        // Arrange
        assertThat(domainUtil.getAllAppointments()).hasSize(1);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/patients/" + patient.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        // Assert
        assertThat(domainUtil.getAllAppointments()).isEmpty();
        assertThat(domainUtil.getAllPatients()).isEmpty();

        assertThat(domainUtil.getLogByTypes(ActionType.DELETE, EntityType.PATIENT)).isNotNull();
        assertThat(domainUtil.getLogByTypes(ActionType.DELETE, EntityType.APPOINTMENT)).isNotNull();
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

    private AppointmentDto updateAppointment(AppointmentRequestDto dto, long id) throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/appointments/" + id)
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        return fromJsonString(mvcResult, AppointmentDto.class);
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