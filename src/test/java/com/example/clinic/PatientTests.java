package com.example.clinic;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.EntityType;
import com.example.clinic.model.Patient;
import com.fasterxml.jackson.core.type.TypeReference;
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
public class PatientTests extends BaseTests {

    private Patient patient;

    @BeforeEach
    public void setUp() {
        patient = domainUtil.createPatient(dummyPatient);
        domainUtil.createPatient(new PersonBaseDto("Xing", "Xong", "xing.xong@email.com", "12345678"));
    }

    @Test
    public void shouldGetPaginatedAndSortedPatients() throws Exception {
        // Act
        var patients = fetchPatients(0, 10);

        // Assert
        assertThat(patients).hasSize(2)
                .map(PersonDto::getFullName).containsExactlyInAnyOrder("Bob Bee", "Xing Xong");
    }

    @Test
    public void shouldGetSecondPageOfPatients() throws Exception {

        // Act
        var patients = fetchPatients(1, 1);

        // Assert
        assertThat(patients).hasSize(1)
                .map(PersonDto::getFullName).containsExactlyInAnyOrder("Xing Xong");
    }

    @Test
    public void shouldCreateNewPatient() throws Exception {

        // Arrange
        var dto = new PersonBaseDto("Sam", "Smith", "sam.brown@email.com", "12345678");

        // Act
        var patient = createPatient(dto);

        // Assert
        assertThat(patient)
                .matches(d -> d.getFullName().equals("Sam Smith"))
                .matches(d -> d.getEmail().equals(dto.getEmail()))
                .matches(d -> d.getCreatedAt() != null);
        assertThat(domainUtil.getAllPatients()).hasSize(3);

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.CREATE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.PATIENT)
                .matches(l -> l.getEntityId().equals(patient.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldForbidCreatingPatientWithSameEmail() throws Exception {

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/patients")
                        .content(asJsonString(dummyPatient))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        assertThat(mvcResult.getResponse().getContentAsString())
                .contains("Patient with email <bob.bee@email.com> already exists");
    }

    @Test
    public void shouldAllowCreatingPatientWithSameEmailAsExistingDoctor() throws Exception {

        // Arrange
        var doctorAsPatient = new PersonBaseDto("Bob", "Bee", ADMIN_EMAIL, "12345678");

        // Act
        var createdPatient = createPatient(doctorAsPatient);

        // Assert
        assertThat(createdPatient)
                .matches(d -> d.getFullName().equals("Bob Bee"))
                .matches(d -> d.getEmail().equals(ADMIN_EMAIL));

        assertThat(domainUtil.getAllPatients()).hasSize(3);
    }

    @Test
    public void shouldGetPatientById() throws Exception {

        // Arrange & Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/" + patient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        var patientDto = fromJsonString(mvcResult, PersonDto.class);

        // Assert
        assertThat(patientDto)
                .matches(d -> d.getFullName().equals(patient.getFullName()))
                .matches(d -> d.getEmail().equals(patient.getEmail()))
                .matches(d -> d.getPassword() == null);     // password is not exposed
    }

    @Test
    public void shouldUpdateExistingPatient() throws Exception {

        // Arrange
        var dto = new PersonBaseDto("Shu", "Shoe", "shu.shoe@email.com", "1234567");

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/patients/" + patient.getId())
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var personDto = fromJsonString(mvcResult, PersonDto.class);
        assertThat(domainUtil.getAllPatients()).hasSize(2);      // no new patient created
        assertThat(personDto)
                .matches(d -> d.getFullName().equals("Shu Shoe"))
                .matches(d -> d.getEmail().equals(dto.getEmail()));

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.UPDATE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.PATIENT)
                .matches(l -> l.getEntityId().equals(personDto.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldMarkPatientAsDeleted() throws Exception {
        // Arrange
        assertThat(domainUtil.getAllPatients()).hasSize(2);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/patients/" + patient.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        // Assert
        assertThat(domainUtil.getAllPatients()).hasSize(1);

        var deletedPatient = dbUtil.doSelect("select * from person where id = " + patient.getId());
        assertThat(deletedPatient).hasSize(1).matches(d -> d.get(0).get("DELETED").equals(true));

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.DELETE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.PATIENT)
                .matches(l -> l.getEntityId().equals(patient.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    @WithMockUser(username = PATIENT_EMAIL, roles = PATIENT)
    public void shouldGetCurrentPatientData() throws Exception {

        // Arrange & Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/current")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        // Assert
        var patientDto = fromJsonString(mvcResult, PersonBaseDto.class);
        assertThat(patientDto)
                .matches(d -> d.getFullName().equals(dummyPatient.getFullName()))
                .matches(d -> d.getEmail().equals(dummyPatient.getEmail()))
                .matches(d -> d.getPassword() == null);     // password is not exposed
    }

    private PersonDto createPatient(PersonBaseDto dto) throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/patients")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, PersonDto.class);
    }

    private List<PersonDto> fetchPatients(int page, int size) throws Exception {
        var mvcResult = mockMvc.perform(get("/api/patients")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, new TypeReference<>() {
        });
    }
}