package model;

import java.time.LocalDate;

public class Prescription {
    private String prescriptionId;
    private String patientId;
    private String drugName;
    private String dosage;
    private int durationDays;
    private String prescribingDoctorId;
    private LocalDate prescribedDate;

    public Prescription(String prescriptionId, String patientId, String drugName, String dosage, int durationDays, String prescribingDoctorId) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.drugName = drugName;
        this.dosage = dosage;
        this.durationDays = durationDays;
        this.prescribingDoctorId = prescribingDoctorId;
        this.prescribedDate = LocalDate.now();
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getDosage() {
        return dosage;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public String getPrescribingDoctorId() {
        return prescribingDoctorId;
    }

    public LocalDate getPrescribedDate() {
        return prescribedDate;
    }

    @Override
    public String toString() {
        return String.format("Rx %s: %s (%s) for %d days\nPrescribed by: %s on %s",
                prescriptionId, drugName, dosage, durationDays,
                prescribingDoctorId, prescribedDate);
    }
}
