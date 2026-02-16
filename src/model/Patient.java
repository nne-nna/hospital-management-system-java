package model;

import java.util.ArrayList;
import java.util.List;

public class Patient extends Person{
    private final String patientId;
    private final List<String> medicalHistory;
    private final List<String> appointmentHistory;
    private final List<String> treatmentHistory;
    private final List<String> prescriptionIds;
    private String assignedDoctorId;

    public Patient(String id, String name, int age, Gender gender, String patientId) {
        super(id, name, age, gender);
        this.patientId = patientId;
        this.medicalHistory = new ArrayList<>();
        this.appointmentHistory = new ArrayList<>();
        this.treatmentHistory = new ArrayList<>();
        this.prescriptionIds = new ArrayList<>();
    }

    public String getPatientId(){
        return patientId;
    }

    public String getAssignedDoctorId(){
        return assignedDoctorId;
    }

    public void assignDoctor(String doctorId) {
        this.assignedDoctorId = doctorId;
    }

    public List<String> getMedicalHistory() {
        return new ArrayList<>(medicalHistory);
    }

    public void addMedicalHistory(String record) {
        medicalHistory.add(record);
    }

    public List<String> getAppointmentHistory(){
        return new ArrayList<>(appointmentHistory);
    }

    public void addAppointment(String appointmentId) {
        appointmentHistory.add(appointmentId);
    }

    public List<String> getTreatmentHistory(){
        return new ArrayList<>(treatmentHistory);
    }

    public void addTreatment(String treatmentId) {
        treatmentHistory.add(treatmentId);
    }

    public List<String> getPrescriptionIds(){
        return new ArrayList<>(prescriptionIds);
    }

    public void addPrescription(String prescriptionId) {
        prescriptionIds.add(prescriptionId);
    }

    @Override
    public String getDisplayInfo(){
        return String.format("Patient: %s (ID: %s) - Age: %d, Gender: %s | Doctor: %s",
                getName(), patientId, getAge(), getGender(),
                assignedDoctorId != null ? assignedDoctorId : "Not Assigned");
    }
}
