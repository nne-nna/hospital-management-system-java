package service;

import model.*;
import repository.PatientRepository;
import repository.PrescriptionRepository;
import repository.StaffRepository;
import repository.TreatmentRepository;
import util.IdGenerator;

import java.util.List;

public class PatientService {
    private PatientRepository patientRepository;
    private StaffRepository staffRepository;
    private PrescriptionRepository prescriptionRepository;
    private TreatmentRepository treatmentRepository;
    private AuthorizationService authService;

    public PatientService(PatientRepository patientRepository, StaffRepository staffRepository, PrescriptionRepository prescriptionRepository, TreatmentRepository treatmentRepository, AuthorizationService authService) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.treatmentRepository = treatmentRepository;
        this.authService = authService;
    }

    public Patient onboardPatient(Staff currentStaff, String name, int age, String gender) throws AuthorizationService.UnauthorizedException {
        //Authorization check
        authService.requirePermission(currentStaff, "ONBOARD_PATIENT");

        //Generate Ids
        String personId = "PER-" + IdGenerator.generatePatientId();
        String patientId = IdGenerator.generatePatientId();

        //Create a patient
        Patient patient = new Patient(personId, name, age, gender, patientId);

        //save to repository
        if(!patientRepository.addPatient(patient)) {
            throw new IllegalStateException("Failed to add patient - Duplicate ID");
        }
        return patient;

    }

    //Assign patient to a doctor. Doctor must exist and must be a doctor.
    public boolean assignPatientToDoctor(Staff currentStaff, String patientId, String doctorId) throws AuthorizationService.UnauthorizedException{
        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        //Validate patient exists
        Patient patient = patientRepository.findById(patientId);
        if(patient == null) {
            System.out.println("/n Patient not found: " + patientId);
        }

        //Validate doctor exists
        Staff staff = staffRepository.findById(doctorId);
        if(staff == null){
            System.out.println("Doctor not found: " + doctorId);
        }

        if(!(staff instanceof Doctor)) {
            System.out.println("Staff member is not a doctor: " + doctorId);
        }

        Doctor doctor = (Doctor) staff;

        //Remove from old doctor if assigned
        assert patient != null;
        if(patient.getAssignedDoctorId() != null) {
            Staff oldStaff = staffRepository.findById(patient.getAssignedDoctorId());
            if (oldStaff instanceof Doctor){
                ((Doctor) oldStaff).removePatient(patientId);
            }
        }

        //Assign patient to a new Doctor
        patient.assignDoctor(doctorId);
        doctor.assignPatient(patientId);

        //Update both in repositories
        patientRepository.updatePatient(patient);
        staffRepository.updateStaff(doctor);

        return true;
    }

    //Add medical history entry for patient
    public void addMedicalHistory(Staff currentStaff, String patientId, String historyEntry) throws AuthorizationService.UnauthorizedException {
        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        Patient patient = patientRepository.findById(patientId);
        if(patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        patient.addMedicalHistory(historyEntry);
        patientRepository.updatePatient(patient);
    }

    //Get complete patient history. Data from multiple sources
    public PatientHistoryReport getPatientHistory(Staff currentStaff, String patientId) throws AuthorizationService.UnauthorizedException {
        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        Patient patient = patientRepository.findById(patientId);
        if(patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        //Related data from other repositories
        List<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId);
        List<TreatmentRecord> treatments = treatmentRepository.findByPatientId(patientId);

        //Get assigned doctor info
        Doctor assignedDoctor = null;
        if(patient.getAssignedDoctorId() != null) {
            Staff staff = staffRepository.findById(patient.getAssignedDoctorId());
            if(staff instanceof Doctor){
                assignedDoctor = (Doctor) staff;
            }
        }

        return new PatientHistoryReport(patient, assignedDoctor, prescriptions, treatments);
    }

    //Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    //Get patients assigned to a specific doctor
    public List<Patient> getpatientsByDoctor(String doctorId) {
        return patientRepository.findByDoctorId(doctorId);
    }

    //Search patients by name
    public List<Patient> searchPatientsByName(String name) {
        return patientRepository.searchByName(name);
    }

    //Get total patient count
    public int getPatientCount() {
        return patientRepository.count();
    }

    //An inner class for the patient history report
    public static class PatientHistoryReport {
        private Patient patient;
        private Doctor assignedDoctor;
        private List<Prescription> prescriptions;
        private List<TreatmentRecord> treatments;

        public PatientHistoryReport(Patient patient, Doctor assignedDoctor,
                                    List<Prescription> prescriptions,
                                    List<TreatmentRecord> treatments) {
            this.patient = patient;
            this.assignedDoctor = assignedDoctor;
            this.prescriptions = prescriptions;
            this.treatments = treatments;
        }

        public Patient getPatient() {
            return patient;
        }

        public Doctor getAssignedDoctor() {
            return assignedDoctor;
        }

        public List<Prescription> getPrescriptions() {
            return prescriptions;
        }

        public List<TreatmentRecord> getTreatments() {
            return treatments;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n========== PATIENT HISTORY REPORT ==========\n");
            sb.append(patient.getDisplayInfo()).append("\n");

            if (assignedDoctor != null) {
                sb.append("\nAssigned Doctor: ").append(assignedDoctor.getName())
                        .append(" (").append(assignedDoctor.getSpecialization()).append(")\n");
            }

            sb.append("\n--- Medical History ---\n");
            List<String> history = patient.getMedicalHistory();
            if (history.isEmpty()) {
                sb.append("No medical history recorded.\n");
            } else {
                for (int i = 0; i < history.size(); i++) {
                    sb.append((i + 1)).append(". ").append(history.get(i)).append("\n");
                }
            }

            sb.append("\n--- Treatments (").append(treatments.size()).append(") ---\n");
            if (treatments.isEmpty()) {
                sb.append("No treatments recorded.\n");
            } else {
                for (TreatmentRecord treatment : treatments) {
                    sb.append(treatment.toString()).append("\n\n");
                }
            }

            sb.append("\n--- Prescriptions (").append(prescriptions.size()).append(") ---\n");
            if (prescriptions.isEmpty()) {
                sb.append("No prescriptions recorded.\n");
            } else {
                for (Prescription rx : prescriptions) {
                    sb.append(rx.toString()).append("\n\n");
                }
            }

            sb.append("==========================================\n");
            return sb.toString();
        }
    }
}
