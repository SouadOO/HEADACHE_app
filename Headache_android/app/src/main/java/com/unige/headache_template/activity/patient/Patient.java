package com.unige.headache_template.activity.patient;

public class Patient {
    private String patientFirstName;
    private String patientLastName;
    private Integer age;
    private String gender;

    public Patient(String patientFirstName, String patientLastName, Integer age, String gender) {
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.age = age;
        this.gender = gender;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public Integer getPatientAge() {
        return age;
    }

    public String getPatientGender() {
        return gender;
    }
}
