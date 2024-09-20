package com.example.clinic;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.EntityType;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;

import static com.example.clinic.BaseControllerTests.ADMIN_EMAIL;
import static com.example.clinic.BaseControllerTests.DOCTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
public class PatientControllerTests extends BaseControllerTests {

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
        var mvcResult = doMvcRequest(dummyPatient, HttpMethod.POST, "/api/patients", status().isBadRequest());

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
        var patientDto = doGetRequest("/api/patients/" + patient.getId(), new TypeReference<PersonDto>() { });

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
        var personDto = doRequest(dto, HttpMethod.PUT, "/api/patients/" + patient.getId(), PersonDto.class);

        // Assert
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
        doDelete("/api/patients/" + patient.getId());

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
        // Act
        var patientDto = doGetRequest("/api/patients/current", new TypeReference<PersonBaseDto>() { });

        // Assert
        assertThat(patientDto)
                .matches(d -> d.getFullName().equals(dummyPatient.getFullName()))
                .matches(d -> d.getEmail().equals(dummyPatient.getEmail()))
                .matches(d -> d.getPassword() == null);     // password is not exposed
    }

    private PersonDto createPatient(PersonBaseDto dto) throws Exception {
        return doCreate(dto, HttpMethod.POST, "/api/patients", PersonDto.class);
    }

    private List<PersonDto> fetchPatients(int page, int size) throws Exception {
        return doGetRequest("/api/patients",
                Map.of("page", String.valueOf(page), "size", String.valueOf(size)),
                new TypeReference<>() { });
    }
}