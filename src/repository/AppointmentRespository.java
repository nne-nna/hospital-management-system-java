package repository;

import model.Appointment;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentRespository {
    private Map<String, Appointment> appointmentMap; //enhances fast ID lookup. Map.get(key) is 0(1)

    public AppointmentRespository() {
        this.appointmentMap = new HashMap<>();
    }

    public boolean addAppointment(Appointment appointment) {
        if(appointmentMap.containsKey(appointment.getAppointmentId())){
            return false;
        }
        appointmentMap.put(appointment.getAppointmentId(), appointment);
        return true;
    }

    public Appointment findById(String appointmentId) {
        return appointmentMap.get(appointmentId);
    }

    public List<Appointment> findAll(){
        return new ArrayList<>(appointmentMap.values());
    }

    //Find all appointments for a patient
    public List<Appointment> findByPatientId(String patientId) {
        return appointmentMap.values().stream()
                .filter(apt -> apt.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(Appointment::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    //Find all appointments for a doctor
    public List<Appointment> findByDoctorId(String doctorId) {
        return appointmentMap.values().stream()
                .filter(apt -> apt.getDoctorId().equals(doctorId))
                .sorted(Comparator.comparing(Appointment::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    //Find appointment by status
    public List<Appointment> findByStatus(Appointment.AppointmentStatus status){
        return appointmentMap.values().stream()
                .filter(apt -> apt.getStatus() == status)
                .collect(Collectors.toList());
    }

    //Find upcoming appointment for a doctor on a specific date.
    public List<Appointment> findByDoctorAndDate(String doctorId, LocalDateTime date) {
        return appointmentMap.values().stream()
                .filter(apt -> apt.getDoctorId().equals(doctorId))
                .filter(apt -> apt.getDateTime().toLocalDate().equals(date.toLocalDate()))
                .filter(apt -> apt.getStatus() == Appointment.AppointmentStatus.SCHEDULED)
                .sorted(Comparator.comparing(Appointment::getDateTime))
                .collect(Collectors.toList());
    }

    //Check if doctor has conflict at any time to prevent double booking.
    public boolean hasConflict(String doctorId, LocalDateTime dateTime) {
        return appointmentMap.values().stream()
                .anyMatch(apt ->
                    apt.getDoctorId().equals(doctorId) &&
                    apt.getStatus() == Appointment.AppointmentStatus.SCHEDULED &&
                    apt.getDateTime().equals(dateTime)
                );
    }

    //update appointment
    public boolean updateAppointment(Appointment appointment) {
        if(!appointmentMap.containsKey(appointment.getAppointmentId())){
            return false;
        }
        appointmentMap.put(appointment.getAppointmentId(), appointment);
        return true;
    }

    public boolean removeAppointment(String appointmentId) {
        return appointmentMap.remove(appointmentId) != null;
    }

    public boolean exists(String appointmentId) {
        return appointmentMap.containsKey(appointmentId);
    }

    public int count() {
        return appointmentMap.size();
    }
}
