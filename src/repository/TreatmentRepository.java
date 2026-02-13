package repository;

import model.TreatmentRecord;

import java.util.*;
import java.util.stream.Collectors;

public class TreatmentRepository {
    public Map<String, TreatmentRecord> treatmentMap;

    public TreatmentRepository() {
        this.treatmentMap = new HashMap<>();
    }

    public boolean addTreatment(TreatmentRecord treatment) {
        if(treatmentMap.containsKey(treatment.getTreatmentId())){
            return false;
        }
        treatmentMap.put(treatment.getTreatmentId(), treatment);
        return true;
    }

    public TreatmentRecord findById(String treatmentId) {
        return treatmentMap.get(treatmentId);
    }

    public List<TreatmentRecord> findAll() {
        return new ArrayList<>(treatmentMap.values());
    }

    //find by patientId
    public List<TreatmentRecord> findByPatientId(String patientId) {
        return treatmentMap.values().stream()
                .filter(t -> t.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(TreatmentRecord::getDate).reversed())
                .collect(Collectors.toList());
    }

    //find the attending doctor
    public List<TreatmentRecord> findByDoctorId(String doctorId) {
        return treatmentMap.values().stream()
                .filter(t -> t.getAttendingDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    public boolean exists(String treatmentId) {
        return treatmentMap.containsKey(treatmentId);
    }

    public int count() {
        return treatmentMap.size();
    }

}
