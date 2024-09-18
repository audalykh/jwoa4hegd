package com.example.clinic;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.model.Patient;
import com.example.clinic.service.LogService;
import com.example.clinic.service.PatientService;
import com.example.clinic.util.DbUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTests {

    protected static final String DOCTOR = "DOCTOR";
    protected static final String PATIENT = "PATIENT";

    protected PersonBaseDto dummyPatient;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Autowired
    protected LogService logService;

    @Autowired
    protected DbUtil dbUtil;

    @BeforeEach
    protected void baseSetup() {
        dummyPatient = new PersonBaseDto("Bob", "Bee", "foo@email.com", "12345678");
    }

    protected Patient createPatient() {
        return patientService.createOrThrow(dummyPatient);
    }
}