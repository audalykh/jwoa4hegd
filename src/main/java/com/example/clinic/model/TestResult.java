package com.example.clinic.model;

import org.apache.commons.lang3.StringUtils;

public enum TestResult {
    POSITIVE,
    NEGATIVE,
    UNKNOWN;

    public String getName() {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
