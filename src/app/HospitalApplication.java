package app;

import model.*;
import repository.*;
import service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HospitalApplication {
    private StaffRepository staffRepository;
    private PatientRepository patientRepository;
    private AppointmentRespository appointmentRepository;
    private PrescriptionRepository prescriptionRepository;
    private TreatmentRepository treatmentRepository;

    // All services
    private AuthorizationService authService;
    private StaffService staffService;
    private PatientService patientService;
    private AppointmentService appointmentService;
    private PrescriptionService prescriptionService;
    private TreatmentService treatmentService;

    // Current logged-in staff
    private Staff currentStaff;
    private final Scanner scanner;

    public HospitalApplication() {
        initializeRepositories();
        initializeServices();
        scanner = new Scanner(System.in);
        seedInitialData();
    }

    //Initialize all repositories
    private void initializeRepositories() {
        staffRepository = new StaffRepository();
        patientRepository = new PatientRepository();
        appointmentRepository = new AppointmentRespository();
        prescriptionRepository = new PrescriptionRepository();
        treatmentRepository = new TreatmentRepository();
    }

    //Initialize all services
    private void initializeServices() {
        authService = new AuthorizationService();

        staffService = new StaffService(staffRepository, authService);

        patientService = new PatientService(
                patientRepository, staffRepository,
                prescriptionRepository, treatmentRepository, authService
        );

        appointmentService = new AppointmentService(
                appointmentRepository, patientRepository,
                staffRepository, authService
        );

        prescriptionService = new PrescriptionService(
                prescriptionRepository, patientRepository,
                staffRepository, authService
        );

        treatmentService = new TreatmentService(
                treatmentRepository, patientRepository,
                staffRepository, authService
        );
    }

    //some initial data for testing
    private void seedInitialData() {
        try {
            // Create initial admin (to onboard others)
            AdminStaff admin = new AdminStaff("PER-ADMIN1", "Alice Admin", 35, Gender.FEMALE,
                    "ADMIN001", "Administration");
            staffRepository.addStaff(admin);

            // Set as current staff for seeding
            currentStaff = admin;

            // Add doctors
            Doctor doctor1 = staffService.onboardDoctor(currentStaff, "John Smith",
                    45, Gender.MALE, "Cardiology", "Cardiologist");

            Doctor doctor2 = staffService.onboardDoctor(currentStaff, "Sarah Johnson",
                    38, Gender.FEMALE, "Pediatrics", "Pediatrician");

            // Add nurses
            staffService.onboardNurse(currentStaff, "Mary Williams",
                    30, Gender.FEMALE, "Emergency", "Ward A");

            // Add patients
            Patient patient1 = patientService.onboardPatient(currentStaff,
                    "Michael Brown", 42, Gender.MALE);

            Patient patient2 = patientService.onboardPatient(currentStaff,
                    "Emily Davis", 28, Gender.FEMALE);

            // Assign patients to doctors
            patientService.assignPatientToDoctor(currentStaff,
                    patient1.getPatientId(), doctor1.getStaffId());

            patientService.assignPatientToDoctor(currentStaff,
                    patient2.getPatientId(), doctor2.getStaffId());

            // Add medical history
            patientService.addMedicalHistory(currentStaff, patient1.getPatientId(),
                    "History of hypertension");

            // Reset current staff (user must login)
            currentStaff = null;

        } catch (Exception e) {
            System.err.println("Error seeding data: " + e.getMessage());
        }
    }

    //Main application loop
    public void run() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   HOSPITAL MANAGEMENT SYSTEM           ║");
        System.out.println("╚════════════════════════════════════════╝");

        while (true) {
            if (currentStaff == null) {
                if (!login()) {
                    System.out.println("Exiting system...");
                    break;
                }
            }

            showMainMenu();
        }

        scanner.close();
    }

    //Login system
    private boolean login() {
        System.out.println("\n--- LOGIN ---");
        System.out.println("Available Staff IDs for testing:");
        for (Staff staff : staffRepository.findAll()) {
            System.out.println("  " + staff.getStaffId() + " - " +
                    staff.getName() + " (" + staff.getRole() + ")");
        }

        System.out.print("\nEnter Staff ID (or 'exit' to quit): ");
        String staffId = scanner.nextLine().trim();

        if (staffId.equalsIgnoreCase("exit")) {
            return false;
        }

        // Case-insensitive lookup
        Staff staff = findStaffByIdCaseInsensitive(staffId);
        if (staff == null) {
            System.out.println("❌ Invalid Staff ID!");
            return login();
        }

        currentStaff = staff;
        System.out.println("\n✅ Welcome, " + currentStaff.getName() +
                " (" + currentStaff.getRole() + ")!");
        return true;
    }

    // Helper method for case-insensitive staff ID lookup
    private Staff findStaffByIdCaseInsensitive(String staffId) {
        for (Staff staff : staffRepository.findAll()) {
            if (staff.getStaffId().equalsIgnoreCase(staffId)) {
                return staff;
            }
        }
        return null;
    }

    // The Main menu
    private void showMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("Logged in as: " + currentStaff.getName() +
                " (" + currentStaff.getRole() + ")");
        System.out.println("1. Staff Management");
        System.out.println("2. Patient Management");
        System.out.println("3. Appointment Management");
        System.out.println("4. Prescription Management");
        System.out.println("5. Treatment Management");
        System.out.println("6. View Patient History");
        System.out.println("7. Logout");
        System.out.println("0. Exit System");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1: staffManagementMenu(); break;
            case 2: patientManagementMenu(); break;
            case 3: appointmentManagementMenu(); break;
            case 4: prescriptionManagementMenu(); break;
            case 5: treatmentManagementMenu(); break;
            case 6: viewPatientHistory(); break;
            case 7: logout(); break;
            case 0: System.exit(0); break;
            default: System.out.println("❌ Invalid option!");
        }
    }

    // ========== STAFF MANAGEMENT (WITH EXCEPTION HANDLING) ==========

    private void staffManagementMenu() {
        System.out.println("\n--- Staff Management ---");
        System.out.println("1. View All Staff");
        System.out.println("2. View All Doctors");
        System.out.println("3. Onboard New Doctor");
        System.out.println("4. Onboard New Nurse");
        System.out.println("5. Onboard New Admin");
        System.out.println("0. Back");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        try {
            switch (choice) {
                case 1: viewAllStaff(); break;
                case 2: viewAllDoctors(); break;
                case 3: onboardDoctor(); break;
                case 4: onboardNurse(); break;
                case 5: onboardAdmin(); break;
                case 0: return;
                default: System.out.println("❌ Invalid option!");
            }
        } catch (AuthorizationService.UnauthorizedException e) {
            System.out.println("❌ UNAUTHORIZED: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ VALIDATION ERROR: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
        }
    }

    private void viewAllStaff() {
        List<Staff> allStaff = staffService.getAllStaff();
        System.out.println("\n========== ALL STAFF (" + allStaff.size() + ") ==========");
        for (Staff staff : allStaff) {
            System.out.println(staff.getDisplayInfo());
        }
    }

    private void viewAllDoctors() {
        List<Staff> doctors = staffService.getAllDoctors();
        System.out.println("\n========== DOCTORS (" + doctors.size() + ") ==========");
        for (Staff staff : doctors) {
            System.out.println(staff.getDisplayInfo());
        }
    }

    private void onboardDoctor() throws AuthorizationService.UnauthorizedException {
        System.out.println("\n--- Onboard New Doctor ---");

        String name = getStringInput("Name: ");
        System.out.print("Age: ");
        int age = getIntInput();
        Gender gender = getGenderInput();
        String department = getStringInput("Department: ");
        String specialization = getStringInput("Specialization: ");

        Doctor doctor = staffService.onboardDoctor(currentStaff, name, age,
                gender, department, specialization);
        System.out.println("✅ Doctor onboarded successfully!");
        System.out.println(doctor.getDisplayInfo());
    }

    private void onboardNurse() throws AuthorizationService.UnauthorizedException {
        System.out.println("\n--- Onboard New Nurse ---");

        String name = getStringInput("Name: ");
        System.out.print("Age: ");
        int age = getIntInput();
        Gender gender = getGenderInput();
        String department = getStringInput("Department: ");
        String ward = getStringInput("Ward: ");

        Nurse nurse = staffService.onboardNurse(currentStaff, name, age,
                gender, department, ward);
        System.out.println("✅ Nurse onboarded successfully!");
        System.out.println(nurse.getDisplayInfo());
    }

    private void onboardAdmin() throws AuthorizationService.UnauthorizedException {
        System.out.println("\n--- Onboard New Admin ---");

        String name = getStringInput("Name: ");
        System.out.print("Age: ");
        int age = getIntInput();
        Gender gender = getGenderInput();
        String department = getStringInput("Department: ");

        AdminStaff admin = staffService.onboardAdmin(currentStaff, name, age,
                gender, department);
        System.out.println("✅ Admin onboarded successfully!");
        System.out.println(admin.getDisplayInfo());
    }

    // ========== PATIENT MANAGEMENT (WITH EXCEPTION HANDLING) ==========

    private void patientManagementMenu() {
        System.out.println("\n--- Patient Management ---");
        System.out.println("1. View All Patients");
        System.out.println("2. Onboard New Patient");
        System.out.println("3. Assign Patient to Doctor");
        System.out.println("4. Search Patient by Name");
        System.out.println("0. Back");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        try {
            switch (choice) {
                case 1: viewAllPatients(); break;
                case 2: onboardPatient(); break;
                case 3: assignPatientToDoctor(); break;
                case 4: searchPatientByName(); break;
                case 0: return;
                default: System.out.println("❌ Invalid option!");
            }
        } catch (AuthorizationService.UnauthorizedException e) {
            System.out.println("❌ UNAUTHORIZED: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
        }
    }

    private void viewAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        System.out.println("\n========== ALL PATIENTS (" + patients.size() + ") ==========");
        for (Patient patient : patients) {
            System.out.println(patient.getDisplayInfo());
        }
    }

    private void onboardPatient() throws AuthorizationService.UnauthorizedException {
        System.out.println("\n--- Onboard New Patient ---");

        String name = getStringInput("Name: ");
        System.out.print("Age: ");
        int age = getIntInput();
        Gender gender = getGenderInput();

        Patient patient = patientService.onboardPatient(currentStaff, name, age, gender);
        System.out.println("✅ Patient onboarded successfully!");
        System.out.println(patient.getDisplayInfo());
    }

    private void assignPatientToDoctor() throws AuthorizationService.UnauthorizedException {
        // Show available doctors first
        List<Staff> doctors = staffService.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("❌ No doctors available. Please onboard a doctor first.");
            return;
        }

        System.out.println("\n--- Available Doctors ---");
        for (Staff doctor : doctors) {
            System.out.println("  " + doctor.getStaffId() + " - " + doctor.getName());
        }
        System.out.println();

        String patientId = getStringInput("Patient ID: ");
        String doctorId = getStringInput("Doctor ID: ");

        patientService.assignPatientToDoctor(currentStaff, patientId, doctorId);
        System.out.println("✅ Patient assigned successfully!");
    }

    private void searchPatientByName() {
        String name = getStringInput("\nEnter name to search: ");

        List<Patient> patients = patientService.searchPatientsByName(name);
        System.out.println("\n========== SEARCH RESULTS (" + patients.size() + ") ==========");
        for (Patient patient : patients) {
            System.out.println(patient.getDisplayInfo());
        }
    }

    // ========== APPOINTMENT MANAGEMENT (WITH EXCEPTION HANDLING) ==========

    private void appointmentManagementMenu() {
        System.out.println("\n--- Appointment Management ---");
        System.out.println("1. View All Appointments");
        System.out.println("2. Schedule New Appointment");
        System.out.println("3. Complete Appointment");
        System.out.println("4. Cancel Appointment");
        System.out.println("5. View Doctor's Schedule");
        System.out.println("0. Back");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        try {
            switch (choice) {
                case 1: viewAllAppointments(); break;
                case 2: scheduleAppointment(); break;
                case 3: completeAppointment(); break;
                case 4: cancelAppointment(); break;
                case 5: viewDoctorSchedule(); break;
                case 0: return;
                default: System.out.println("❌ Invalid option!");
            }
        } catch (AuthorizationService.UnauthorizedException e) {
            System.out.println("❌ UNAUTHORIZED: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("❌ INVALID DATE FORMAT: Use yyyy-MM-dd HH:mm (e.g., 2024-12-25 14:30)");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("❌ CONFLICT: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
        }
    }

    private void viewAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        System.out.println("\n========== ALL APPOINTMENTS (" + appointments.size() + ") ==========");
        for (Appointment apt : appointments) {
            System.out.println(apt);
        }
    }

    private void scheduleAppointment() throws AuthorizationService.UnauthorizedException {
        System.out.println("\n--- Schedule Appointment ---");

        String patientId = getStringInput("Patient ID: ");
        String doctorId = getStringInput("Doctor ID: ");
        String dateTimeStr = getStringInput("Date/Time (yyyy-MM-dd HH:mm): ");

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Appointment apt = appointmentService.scheduleAppointment(
                currentStaff, patientId, doctorId, dateTime);
        System.out.println("✅ Appointment scheduled!");
        System.out.println(apt);
    }

    private void completeAppointment() throws AuthorizationService.UnauthorizedException {
        String aptId = getStringInput("\nAppointment ID: ");

        appointmentService.completeAppointment(currentStaff, aptId);
        System.out.println("✅ Appointment marked as completed!");
    }

    private void cancelAppointment() throws AuthorizationService.UnauthorizedException {
        String aptId = getStringInput("\nAppointment ID: ");

        appointmentService.cancelAppointment(currentStaff, aptId);
        System.out.println("✅ Appointment cancelled!");
    }

    private void viewDoctorSchedule() {
        try {
            String doctorId = getStringInput("\nDoctor ID: ");
            String dateStr = getStringInput("Date (yyyy-MM-dd): ");

            LocalDateTime date = LocalDateTime.parse(dateStr + " 00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            List<Appointment> schedule = appointmentService.getDoctorSchedule(doctorId, date);
            System.out.println("\n========== SCHEDULE (" + schedule.size() + ") ==========");
            for (Appointment apt : schedule) {
                System.out.println(apt);
            }
        } catch (DateTimeParseException e) {
            System.out.println("❌ INVALID DATE FORMAT: Use yyyy-MM-dd (e.g., 2024-12-25)");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }
    }

    // ========== PRESCRIPTION MANAGEMENT (WITH EXCEPTION HANDLING) ==========

    private void prescriptionManagementMenu() {
        System.out.println("\n--- Prescription Management ---");
        System.out.println("1. View All Prescriptions");
        System.out.println("2. Create New Prescription");
        System.out.println("3. View Patient Prescriptions");
        System.out.println("0. Back");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        try {
            switch (choice) {
                case 1: viewAllPrescriptions(); break;
                case 2: createPrescription(); break;
                case 3: viewPatientPrescriptions(); break;
                case 0: return;
                default: System.out.println("❌ Invalid option!");
            }
        } catch (AuthorizationService.UnauthorizedException e) {
            System.out.println("❌ UNAUTHORIZED: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
        }
    }

    private void viewAllPrescriptions() throws AuthorizationService.UnauthorizedException {
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions(currentStaff);
        System.out.println("\n========== ALL PRESCRIPTIONS (" + prescriptions.size() + ") ==========");
        for (Prescription rx : prescriptions) {
            System.out.println(rx);
            System.out.println();
        }
    }

    private void createPrescription() throws AuthorizationService.UnauthorizedException {
        System.out.println("\n--- Create Prescription ---");

        String patientId = getStringInput("Patient ID: ");
        String drugName = getStringInput("Drug Name: ");
        String dosage = getStringInput("Dosage: ");
        System.out.print("Duration (days): ");
        int duration = getIntInput();

        Prescription rx = prescriptionService.createPrescription(
                currentStaff, patientId, drugName, dosage, duration);
        System.out.println("✅ Prescription created!");
        System.out.println(rx);
    }

    private void viewPatientPrescriptions() throws AuthorizationService.UnauthorizedException {
        String patientId = getStringInput("\nPatient ID: ");

        List<Prescription> prescriptions = prescriptionService.getPatientPrescriptions(
                currentStaff, patientId);
        System.out.println("\n========== PRESCRIPTIONS (" + prescriptions.size() + ") ==========");
        for (Prescription rx : prescriptions) {
            System.out.println(rx);
            System.out.println();
        }
    }

    // ========== TREATMENT MANAGEMENT (WITH EXCEPTION HANDLING) ==========

    private void treatmentManagementMenu() {
        System.out.println("\n--- Treatment Management ---");
        System.out.println("1. View All Treatments");
        System.out.println("2. Record New Treatment");
        System.out.println("3. View Patient Treatments");
        System.out.println("0. Back");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        try {
            switch (choice) {
                case 1: viewAllTreatments(); break;
                case 2: recordTreatment(); break;
                case 3: viewPatientTreatments(); break;
                case 0: return;
                default: System.out.println("❌ Invalid option!");
            }
        } catch (AuthorizationService.UnauthorizedException e) {
            System.out.println("❌ UNAUTHORIZED: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
        }
    }

    private void viewAllTreatments() throws AuthorizationService.UnauthorizedException {
        List<TreatmentRecord> treatments = treatmentService.getAllTreatments(currentStaff);
        System.out.println("\n========== ALL TREATMENTS (" + treatments.size() + ") ==========");
        for (TreatmentRecord treatment : treatments) {
            System.out.println(treatment);
            System.out.println();
        }
    }

    private void recordTreatment() throws AuthorizationService.UnauthorizedException {
        System.out.println("\n--- Record Treatment ---");

        String patientId = getStringInput("Patient ID: ");
        String diagnosis = getStringInput("Diagnosis: ");
        String notes = getStringInput("Treatment Notes: ");

        TreatmentRecord treatment = treatmentService.recordTreatment(
                currentStaff, patientId, diagnosis, notes);
        System.out.println("✅ Treatment recorded!");
        System.out.println(treatment);
    }

    private void viewPatientTreatments() throws AuthorizationService.UnauthorizedException {
        String patientId = getStringInput("\nPatient ID: ");

        List<TreatmentRecord> treatments = treatmentService.getPatientTreatments(
                currentStaff, patientId);
        System.out.println("\n========== TREATMENTS (" + treatments.size() + ") ==========");
        for (TreatmentRecord treatment : treatments) {
            System.out.println(treatment);
            System.out.println();
        }
    }

    // ========== VIEW PATIENT HISTORY (WITH EXCEPTION HANDLING) ==========

    private void viewPatientHistory() {
        try {
            String patientId = getStringInput("\nPatient ID: ");

            PatientService.PatientHistoryReport report =
                    patientService.getPatientHistory(currentStaff, patientId);

            System.out.println(report);

        } catch (AuthorizationService.UnauthorizedException e) {
            System.out.println("❌ UNAUTHORIZED: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
        }
    }

    // ========== UTILITY METHODS ==========

    private void logout() {
        System.out.println("\n👋 Goodbye, " + currentStaff.getName() + "!");
        currentStaff = null;
    }

    private int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.print("Please enter a number: ");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number. Please try again: ");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private Gender getGenderInput() {
        while (true) {
            try {
                String input = getStringInput("Gender (Male/Female): ");
                return Gender.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        HospitalApplication app = new HospitalApplication();
        app.run();
    }
}