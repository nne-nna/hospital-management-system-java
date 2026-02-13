package service;

import model.Patient;
import model.Prescription;
import model.Staff;
import repository.PatientRepository;
import repository.PrescriptionRepository;
import repository.StaffRepository;
import util.IdGenerator;

import java.util.List;

public class PrescriptionService {
    private PrescriptionRepository prescriptionRepository;
    private PatientRepository patientRepository;
    private StaffRepository staffRepository;
    private AuthorizationService authService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PatientRepository patientRepository,
                               StaffRepository staffRepository,
                               AuthorizationService authService) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.authService = authService;
    }

    //Create a new prescription. Only doctors can prescribe medication.
    public Prescription createPrescription(Staff currentStaff, String patientId,
                                           String drugName, String dosage,
                                           int durationDays)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "PRESCRIBE");

        // Validate patient exists
        Patient patient = patientRepository.findById(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        // Validate input
        if (drugName == null || drugName.trim().isEmpty()) {
            throw new IllegalArgumentException("Drug name cannot be empty");
        }
        if (dosage == null || dosage.trim().isEmpty()) {
            throw new IllegalArgumentException("Dosage cannot be empty");
        }
        if (durationDays <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        // Create prescription
        String prescriptionId = IdGenerator.generatePrescriptionId();
        Prescription prescription = new Prescription(
                prescriptionId, patientId, drugName, dosage,
                durationDays, currentStaff.getStaffId()
        );

        // Save prescription
        if (!prescriptionRepository.addPrescription(prescription)) {
            throw new IllegalStateException("Failed to create prescription");
        }

        // Add to patient's record
        patient.addPrescription(prescriptionId);
        patientRepository.updatePatient(patient);

        return prescription;
    }

    //Get all prescriptions for a patient
    public List<Prescription> getPatientPrescriptions(Staff currentStaff, String patientId)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "VIEW_HISTORY");
        return prescriptionRepository.findByPatientId(patientId);
    }

    //Get prescriptions by doctor
    public List<Prescription> getDoctorPrescriptions(Staff currentStaff, String doctorId)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "VIEW_HISTORY");
        return prescriptionRepository.findByDoctorId(doctorId);
    }

    //Search prescriptions by drug name
    public List<Prescription> searchByDrugName(Staff currentStaff, String drugName)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "VIEW_HISTORY");
        return prescriptionRepository.findByDrugName(drugName);
    }

    //Find prescription by ID
    public Prescription findPrescriptionById(String prescriptionId) {
        return prescriptionRepository.findById(prescriptionId);
    }

    //Get all prescriptions
    public List<Prescription> getAllPrescriptions(Staff currentStaff)
            throws AuthorizationService.UnauthorizedException {

        authService.requirePermission(currentStaff, "VIEW_HISTORY");
        return prescriptionRepository.findAll();
    }
}
