package com.example.clinic.repository.spec;

import com.example.clinic.model.Patient;
import com.example.clinic.model.Test;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class TestSpec {

    public Specification<Test> patientEq(Patient patient) {
        return (root, query, cb) -> cb.equal(root.join("appointment").get("patient"), patient);
    }
}