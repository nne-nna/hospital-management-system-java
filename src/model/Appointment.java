package model;

import java.time.LocalDateTime;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private LocalDateTime dateTime;
    private AppointmentStatus status;

    public enum AppointmentStatus{
        SCHEDULED,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    public Appointment(String appointmentId, String patientId, String doctorId, LocalDateTime dateTime) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Appointment %s: Patient %s with Doctor %s on %s [%s]",
                appointmentId, patientId, doctorId, dateTime, status);
    }
}
