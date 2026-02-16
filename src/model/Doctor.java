package model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends Staff {
    private String specialization;
    private List<String> assignedPatientsIds;

    public Doctor(String id, String name, int age, Gender gender, String staffId, String department, String specialization) {
        super(id, name, age, gender, staffId, "Doctor", department);
        this.specialization = specialization;
        this.assignedPatientsIds = new ArrayList<>();
    }

    public String getSpecialization() {
        return specialization;
    }

    public List<String> getAssignedPatientIds() {
        return new ArrayList<>(assignedPatientsIds);
    }

    public void assignPatient(String patientId) {
        if(!assignedPatientsIds.contains(patientId)){
            assignedPatientsIds.add(patientId);
        }
    }

    public void removePatient(String patientId) {
        assignedPatientsIds.remove(patientId);
    }

    @Override
    public boolean canPrescribeMedication(){
        return true;
    }

    @Override
    public boolean canOnboardPatients(){
        return false;
    }

    @Override
    public boolean canOnboardStaff(){
        return false;
    }

    @Override
    public String getDisplayInfo(){
        return String.format("Dr. %s (%s) - %s | Dept: %s | Patients: %d",
                getName(), getStaffId(), specialization, getDepartment(),
                assignedPatientsIds.size());
    }
}
