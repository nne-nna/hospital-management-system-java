package service;

import model.Appointment;
import model.Doctor;
import model.Patient;
import model.Staff;
import repository.AppointmentRespository;
import repository.PatientRepository;
import repository.StaffRepository;
import util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {
    private AppointmentRespository appointmentRespository;
    private PatientRepository patientRepository;
    private StaffRepository staffRepository;
    private AuthorizationService authService;

    public AppointmentService(AppointmentRespository appointmentRespository, PatientRepository patientRepository, StaffRepository staffRepository, AuthorizationService authService) {
        this.appointmentRespository = appointmentRespository;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.authService = authService;
    }

    //patient and doctor must exist. no scheculing conflicts.
    public Appointment scheduleAppointment(Staff currentStaff, String patientId, String doctorId, LocalDateTime dateTime) throws AuthorizationService.UnauthorizedException{
        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        //Validate patient
        Patient patient = patientRepository.findById(patientId);
        if(patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        //Validate doctor
        Staff staff = staffRepository.findById(doctorId);
        if(staff == null) {
            throw new IllegalArgumentException("Doctor not found: " + doctorId);
        }

        if(!(staff instanceof Doctor)) {
            throw new IllegalArgumentException("Staff member is not a doctor: " + doctorId);
        }

        //check for scheduling conflict
        if(appointmentRespository.hasConflict(doctorId, dateTime)){
            throw new IllegalStateException(
                    "Doctor already has an appointment at " + dateTime
            );
        }

        //Create an appointment
        String appointmentId = IdGenerator.generateAppointmentId();
        Appointment appointment = new Appointment(appointmentId, patientId, doctorId, dateTime);

        //Save appointment
        if(!appointmentRespository.addAppointment(appointment)){
            throw new IllegalStateException("Failed to create appointment");
        }

        //Add to patient's history
        patient.addAppointment(appointmentId);
        patientRepository.updatePatient(patient);

        return appointment;
    }

    //Update appointment status
    public boolean updateAppointmentStatus(Staff currentStaff, String appointmentId, Appointment.AppointmentStatus newStatus) throws AuthorizationService.UnauthorizedException{
        authService.requirePermission(currentStaff, "VIEW_HISTORY");

        Appointment appointment = appointmentRespository.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }
        appointment.setStatus(newStatus);
        return appointmentRespository.updateAppointment(appointment);
    }

    //Cancel an appointment
    public boolean cancelAppointment(Staff currentStaff, String appointmentId)
            throws AuthorizationService.UnauthorizedException {

        return updateAppointmentStatus(currentStaff, appointmentId,
                Appointment.AppointmentStatus.CANCELLED);
    }

    //complete an appointment
    public boolean completeAppointment(Staff currentStaff, String appointmentId)
            throws AuthorizationService.UnauthorizedException {

        return updateAppointmentStatus(currentStaff, appointmentId,
                Appointment.AppointmentStatus.COMPLETED);
    }

    //Get all appointments for a patient
    public List<Appointment> getPatientAppointments(String patientId) {
        return appointmentRespository.findByPatientId(patientId);
    }

    //Get all appointments for a doctor
    public List<Appointment> getDoctorAppointments(String doctorId) {
        return appointmentRespository.findByDoctorId(doctorId);
    }

    //Get doctor's appointments for a specific date
    public List<Appointment> getDoctorSchedule(String doctorId, LocalDateTime date) {
        return appointmentRespository.findByDoctorAndDate(doctorId, date);
    }

    //Get all scheduled (upcoming) appointments
    public List<Appointment> getScheduledAppointments() {
        return appointmentRespository.findByStatus(Appointment.AppointmentStatus.SCHEDULED);
    }

    //Find appointment by ID
    public Appointment findAppointmentById(String appointmentId) {
        return appointmentRespository.findById(appointmentId);
    }

    //Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRespository.findAll();
    }
}
