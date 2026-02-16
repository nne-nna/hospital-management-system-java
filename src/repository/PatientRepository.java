package repository;

import model.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientRepository {
    private Map<String, Patient> patientMap;

    public PatientRepository() {
        this.patientMap = new HashMap<>();
    }

    //Register a new patient
    public boolean addPatient(Patient patient){
        if(patientMap.containsKey(patient.getPatientId())){
            return false;
        }
        patientMap.put(patient.getPatientId(), patient);
        return true;
    }

    //Find patient by ID
    public Patient findById(String patientId) {
        return patientMap.get(patientId);
    }

    //Get all patients
    public List<Patient> findAll() {
        return new ArrayList<>(patientMap.values());
    }

    //Finds patient assigned to a specific doctor. Useful for doctor's patient list
    public List<Patient> findByDoctorId(String doctorId) {
        List<Patient> result = new ArrayList<>();
        for(Patient patient : patientMap.values()) {
            if(doctorId.equals(patient.getAssignedDoctorId())){
                result.add(patient);
            }
        }
        return result;
    }

    //Search patient by name, case insensitive... and partial march
    public List<Patient> searchByName(String namePart) {
        List<Patient> result = new ArrayList<>();
        String searchLower = namePart.toLowerCase();
        for(Patient patient : patientMap.values()) {
            if (patient.getName().toLowerCase().contains(searchLower)) {
                result.add(patient);
            }
        }
        return result;
    }

    //update patient information
    public boolean updatePatient(Patient patient) {
        if(!patientMap.containsKey(patient.getPatientId())) {
            return false;
        }
        patientMap.put(patient.getPatientId(), patient);
        return true;
    }

    //remove patient
    public boolean removePatient(String patientId) {
        return patientMap.remove(patientId) != null;
    }

    //if patient exists
    public boolean exists(String patientId) {
        return patientMap.containsKey(patientId);
    }

    //patient size
    public int count() {
        return patientMap.size();
    }

}
