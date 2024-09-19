package com.example.clinic;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.model.Doctor;
import com.example.clinic.util.DbUtil;
import com.example.clinic.util.DomainUtil;
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
public abstract class BaseTests {

    protected static final String DOCTOR = "DOCTOR";
    protected static final String PATIENT = "PATIENT";

    protected static final String ADMIN_EMAIL = "admin@achme.com";
    protected static final String PATIENT_EMAIL = "bob.bee@email.com";

    protected PersonBaseDto dummyPatient;
    protected PersonBaseDto dummyDoctor;

    protected Doctor adminDoctor;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected DbUtil dbUtil;

    @Autowired
    protected DomainUtil domainUtil;

    @BeforeEach
    protected void baseSetup() {
        dummyPatient = new PersonBaseDto("Bob", "Bee", PATIENT_EMAIL, "12345678");
        dummyDoctor = new PersonBaseDto("Donny", "Doe", "alice.doe@email.com", "12345678");
        adminDoctor = domainUtil.getDoctorByEmail(ADMIN_EMAIL);
    }
}