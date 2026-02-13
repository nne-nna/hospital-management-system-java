package service;

import model.Staff;

public class AuthorizationService {
    public boolean canOnboardPatients(Staff staff){
        if(staff == null){
            return false;
        }
        return staff.canOnboardPatients();
    }

    public boolean canOnboardStaff(Staff staff) {
        if(staff == null){
            return false;
        }
        return staff.canOnboardStaff();
    }

    public boolean canPrescribeMedication(Staff staff){
        if(staff == null){
            return false;
        }
        return staff.canPrescribeMedication();
    }

    public boolean canViewPatientHistory(Staff staff){
        return staff != null;
    }

    public void requirePermission(Staff staff, String action) throws UnauthorizedException {
        switch (action.toUpperCase()) {
            case "ONBOARD_PATIENT":
                if (!canOnboardPatients(staff)) {
                    throw new UnauthorizedException(
                            "Only Admin staff can onboard patients. Your role: " + staff.getRole());
                }
                break;
            case "ONBOARD_STAFF":
                if (!canOnboardStaff(staff)) {
                    throw new UnauthorizedException(
                            "Only Admin staff can onboard staff. Your role: " + staff.getRole());
                }
                break;
            case "PRESCRIBE":
                if (!canPrescribeMedication(staff)) {
                    throw new UnauthorizedException(
                            "Only Doctors can prescribe medication. Your role: " + staff.getRole());
                }
                break;
            case "VIEW_HISTORY":
                if (!canViewPatientHistory(staff)) {
                    throw new UnauthorizedException("Staff not authenticated.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    //Custom exception for authorization failures
    public static class UnauthorizedException extends Exception {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
