package repository;

import model.Prescription;

import java.util.*;
import java.util.stream.Collectors;

public class PrescriptionRepository {
    private Map<String, Prescription> prescriptionMap;

    public PrescriptionRepository() {
        this.prescriptionMap = new HashMap<>();
    }

    public boolean addPrescription(Prescription prescription) {
        if(prescriptionMap.containsKey(prescription.getPrescriptionId())) {
            return false;
        }
        prescriptionMap.put(prescription.getPrescriptionId(), prescription);
        return true;
    }

    public Prescription findById(String prescriptionId) {
        return prescriptionMap.get(prescriptionId);
    }

    public List<Prescription> findAll() {
        return new ArrayList<>(prescriptionMap.values());
    }

    //Find all prescriptions for a patient
    public List<Prescription> findByPatientId(String patientId) {
        return prescriptionMap.values().stream()
                .filter(rx -> rx.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(Prescription::getPrescribedDate).reversed())
                .collect(Collectors.toList());
    }

    //Find prescription by prescribing doctor
    public List<Prescription> findByDoctorId(String doctorId){
        return prescriptionMap.values().stream()
                .filter(rx -> rx.getPrescribingDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public List<Prescription> findByDrugName(String drugName) {
        return prescriptionMap.values().stream()
                .filter(rx -> rx.getDrugName().equalsIgnoreCase(drugName))
                .collect(Collectors.toList());
    }

    public boolean exists(String prescriptionId) {
        return prescriptionMap.containsKey(prescriptionId);
    }

    public int count() {
        return prescriptionMap.size();
    }

}
