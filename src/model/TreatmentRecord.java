package model;

import java.time.LocalDate;

public class TreatmentRecord {
    private String treatmentId;
    private String patientId;
    private String diagnosis;
    private String treatmentNotes;
    private LocalDate date;
    private String attendingDoctorId;

    public TreatmentRecord(String treatmentId, String patientId, String diagnosis, String treatmentNotes, LocalDate date, String attendingDoctorId) {
        this.treatmentId = treatmentId;
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.treatmentNotes = treatmentNotes;
        this.date = date;
        this.attendingDoctorId = attendingDoctorId;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatmentNotes() {
        return treatmentNotes;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getAttendingDoctorId() {
        return attendingDoctorId;
    }

    @Override
    public String toString() {
        return String.format("Treatment %s on %s: %s\nNotes: %s\nDoctor: %s",
                treatmentId, date, diagnosis, treatmentNotes, attendingDoctorId);
    }
}
