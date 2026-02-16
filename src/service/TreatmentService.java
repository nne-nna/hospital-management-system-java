package service;

import model.Patient;
import model.Staff;
import model.TreatmentRecord;
import repository.PatientRepository;
import repository.StaffRepository;
import repository.TreatmentRepository;
import util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

public class TreatmentService {
    private TreatmentRepository treatmentRepository;
    private PatientRepository patientRepository;
    private StaffRepository staffRepository;
    private AuthorizationService authService;

    public TreatmentService(TreatmentRepository treatmentRepository,
                            PatientRepository patientRepository,
                            StaffRepository staffRepository,
                            AuthorizationService authService) {
        this.treatmentRepository = treatmentRepository;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.authService = authService;
    }

    // Record a new treatment. Done by doctors after appointments
    public TreatmentRecord recordTreatment(Staff currentStaff, String patientId,
                                           String diagnosis, String treatmentNotes)
            throws AuthorizationService.UnauthorizedException {

        // Any staff can record treatments, most likely doctors.
        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        // Check if patient exists
        Patient patient = patientRepository.findById(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        // Validate input
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be empty");
        }

        // Treatment notes can be optional, but normalize
        if (treatmentNotes == null) {
            treatmentNotes = "";
        }

        // Create treatment record
        String treatmentId = IdGenerator.generateTreatmentId();
        TreatmentRecord treatment = new TreatmentRecord(
                treatmentId, patientId, diagnosis, treatmentNotes,
                LocalDate.now(), currentStaff.getStaffId()
        );

        // Save treatment
        if (!treatmentRepository.addTreatment(treatment)) {
            throw new IllegalStateException("Failed to record treatment. ID may be duplicate: " + treatmentId);
        }

        // Add to patient's history
        patient.addTreatment(treatmentId);
        patientRepository.updatePatient(patient);

        return treatment;
    }

    // Get all treatments for a patient
    public List<TreatmentRecord> getPatientTreatments(Staff currentStaff, String patientId)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }

        return treatmentRepository.findByPatientId(patientId);
    }

    // Get treatments by doctor
    public List<TreatmentRecord> getDoctorTreatments(Staff currentStaff, String doctorId)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be empty");
        }

        return treatmentRepository.findByDoctorId(doctorId);
    }

    // Find treatment by ID
    public TreatmentRecord findTreatmentById(String treatmentId) {
        if (treatmentId == null || treatmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment ID cannot be empty");
        }

        TreatmentRecord treatment = treatmentRepository.findById(treatmentId);
        if (treatment == null) {
            throw new IllegalArgumentException("Treatment not found: " + treatmentId);
        }

        return treatment;
    }

    // Get all treatments
    public List<TreatmentRecord> getAllTreatments(Staff currentStaff)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "VIEW_HISTORY");
        return treatmentRepository.findAll();
    }
}