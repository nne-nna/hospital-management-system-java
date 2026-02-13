package util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger patientCounter = new AtomicInteger(1000);
    private static final AtomicInteger staffCounter = new AtomicInteger(2000);
    private static final AtomicInteger appointmentCounter = new AtomicInteger(3000);
    private static final AtomicInteger treatmentCounter = new AtomicInteger(4000);
    private static final AtomicInteger prescriptionCounter = new AtomicInteger(5000);

    public static String generatePatientId(){
        return "P" + patientCounter.incrementAndGet();
    }

    public static String generateStaffId(){
        return "S" + staffCounter.incrementAndGet();
    }

    public static String generateAppointmentId(){
        return "A" + appointmentCounter.incrementAndGet();
    }

    public static String generateTreatmentId(){
        return "T" + treatmentCounter.incrementAndGet();
    }

    public static String generatePrescriptionId(){
        return "Rx" + prescriptionCounter.incrementAndGet();
    }
}
