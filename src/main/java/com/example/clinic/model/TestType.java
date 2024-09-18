package com.example.clinic.model;

public enum TestType {
    TEST1,
    TEST2,
    TEST3,
    TEST4,
    TEST5,
    TEST6,
    TEST7,
    TEST8,
    TEST9,
    TEST10;

    public String getName() {
        return "Test " + (ordinal() + 1);
    }

    @Override
    public String toString() {
        return getName();
    }
}
